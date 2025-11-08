#!/bin/bash

# Script para corrigir configuração do nginx na EC2
echo "=== CORRIGINDO NGINX NA EC2 ==="
echo "Data: $(date)"
echo ""

# 1. Verificar docker-compose.yml atual
echo "1. Verificando docker-compose.yml..."
if [ ! -f "docker-compose.yml" ]; then
    echo "   ❌ docker-compose.yml não encontrado no diretório atual"
    echo "   Procurando..."
    DOCKER_COMPOSE=$(find ~ -name "docker-compose.yml" -type f 2>/dev/null | grep -v node_modules | head -1)
    if [ -n "$DOCKER_COMPOSE" ]; then
        echo "   Encontrado em: $DOCKER_COMPOSE"
        cd "$(dirname "$DOCKER_COMPOSE")"
    else
        echo "   ❌ docker-compose.yml não encontrado"
        exit 1
    fi
fi

# 2. Criar/atualizar nginx.conf correto
echo ""
echo "2. Criando nginx.conf correto..."
cat > nginx.conf << 'EOF'
server {
  listen 80;
  server_name _;

  root /usr/share/nginx/html;
  index index.html;

  # estáticos
  location ~* \.(?:js|css|png|jpg|jpeg|gif|svg|ico|woff2?)$ {
    try_files $uri =404;
    add_header Cache-Control "public, max-age=31536000, immutable";
    access_log off;
  }

  # SPA
  location / {
    try_files $uri $uri/ /index.html;
  }

  # API - Proxy correto
  location /api/ {
    proxy_pass http://app:8080/api/;   # <-- com /api/ no final para manter o path
    proxy_http_version 1.1;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto $scheme;
    proxy_buffering off;
    proxy_redirect off;
  }
}
EOF
echo "   ✅ nginx.conf criado"

# 3. Verificar se docker-compose usa build ou volume
echo ""
echo "3. Verificando configuração do docker-compose..."
if grep -q "build:" docker-compose.yml && grep -q "web:" docker-compose.yml; then
    echo "   ✅ Container web usa BUILD"
    echo "   Reconstruindo container web..."
    sudo docker-compose build web
    sudo docker-compose up -d web
    echo "   ✅ Container web reconstruído"
elif grep -q "volumes:" docker-compose.yml && grep -q "nginx.conf" docker-compose.yml; then
    echo "   ✅ Container web usa VOLUMES com nginx.conf"
    echo "   Recarregando nginx..."
    sudo docker exec geo7-web nginx -s reload 2>/dev/null || echo "   (Nginx será recarregado ao reiniciar)"
    echo "   Reiniciando container web..."
    sudo docker-compose restart web
    echo "   ✅ Container web reiniciado"
else
    echo "   ⚠️ Configuração não identificada claramente"
    echo "   Tentando recarregar nginx..."
    sudo docker exec geo7-web nginx -s reload 2>/dev/null || echo "   (Nginx não respondeu ao reload)"
    echo "   Reiniciando container web..."
    sudo docker-compose restart web || sudo docker restart geo7-web
fi

# 4. Verificar se nginx.conf foi aplicado
echo ""
echo "4. Verificando se nginx.conf foi aplicado..."
sleep 3
NGINX_CONFIG=$(sudo docker exec geo7-web cat /etc/nginx/conf.d/default.conf 2>/dev/null)
if [ -n "$NGINX_CONFIG" ]; then
    if echo "$NGINX_CONFIG" | grep -q "proxy_pass.*app:8080/api/"; then
        echo "   ✅ nginx.conf correto aplicado no container"
    else
        echo "   ⚠️ nginx.conf pode não estar correto"
        echo "   Configuração atual do proxy:"
        echo "$NGINX_CONFIG" | grep -A 3 "location /api/"
    fi
else
    echo "   ⚠️ Não foi possível ler nginx.conf do container"
fi

# 5. Testar proxy
echo ""
echo "5. Testando proxy..."
sleep 2
PROXY_TEST=$(curl -s -o /dev/null -w "%{http_code}" http://localhost/api/healthz 2>/dev/null)
if [ "$PROXY_TEST" = "200" ]; then
    echo "   ✅ Proxy funcionando (HTTP $PROXY_TEST)"
else
    echo "   ⚠️ Proxy retornou código: $PROXY_TEST"
fi

# 6. Verificar build do frontend
echo ""
echo "6. Verificando build do frontend..."
MAIN_JS=$(find /var/www/html -name "main*.js" -type f 2>/dev/null | head -1)
if [ -n "$MAIN_JS" ]; then
    if grep -q "https://18-228-94-6.sslip.io" "$MAIN_JS" 2>/dev/null; then
        echo "   ❌ PROBLEMA: Build contém URLs absolutas HTTPS!"
        echo "   Isso causa o erro de tentar usar HTTPS quando deveria usar HTTP"
        echo "   Solução: Rebuild do frontend localmente com 'npm run build:prod'"
    else
        echo "   ✅ Build parece correto (sem URLs absolutas HTTPS)"
    fi
fi

# Resumo
echo ""
echo "=== CORREÇÃO CONCLUÍDA ==="
echo ""
echo "Próximos passos se o problema persistir:"
echo "1. Limpar cache do navegador (Ctrl+Shift+R)"
echo "2. Verificar HSTS: chrome://net-internals/#hsts"
echo "3. Rebuild do frontend localmente com 'npm run build:prod'"
echo "4. Fazer upload novamente"
echo ""
echo "Para verificar logs:"
echo "  sudo docker logs geo7-web"
echo "  sudo docker logs geo7-app"
echo ""

