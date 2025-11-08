#!/bin/bash

# Script completo para corrigir nginx na EC2
echo "=== CORRIGINDO NGINX COMPLETO NA EC2 ==="
echo "Data: $(date)"
echo ""

# 1. Verificar qual docker-compose está sendo usado
echo "1. Verificando docker-compose.yml..."
DOCKER_COMPOSE=""
if [ -f "docker-compose.yml" ]; then
    DOCKER_COMPOSE="docker-compose.yml"
    echo "   ✅ docker-compose.yml encontrado no diretório atual"
elif [ -f "/home/ubuntu/geo7/docker/docker-compose.yml" ]; then
    DOCKER_COMPOSE="/home/ubuntu/geo7/docker/docker-compose.yml"
    cd /home/ubuntu/geo7/docker
    echo "   ✅ docker-compose.yml encontrado em /home/ubuntu/geo7/docker"
else
    DOCKER_COMPOSE=$(find ~ -name "docker-compose.yml" -type f 2>/dev/null | grep -v node_modules | head -1)
    if [ -n "$DOCKER_COMPOSE" ]; then
        cd "$(dirname "$DOCKER_COMPOSE")"
        echo "   ✅ docker-compose.yml encontrado em: $(dirname "$DOCKER_COMPOSE")"
    else
        echo "   ❌ docker-compose.yml não encontrado"
        exit 1
    fi
fi

# 2. Verificar como o container web está configurado
echo ""
echo "2. Verificando configuração do container web..."
if grep -q "volumes:" "$DOCKER_COMPOSE" && grep -q "nginx.conf" "$DOCKER_COMPOSE"; then
    echo "   ✅ Container web usa VOLUMES com nginx.conf"
    USE_VOLUMES=true
    NGINX_CONF_PATH="nginx.conf"
elif grep -q "build:" "$DOCKER_COMPOSE" && grep -q "web:" "$DOCKER_COMPOSE"; then
    echo "   ⚠️ Container web usa BUILD"
    USE_VOLUMES=false
    NGINX_CONF_PATH="nginx.conf"
else
    echo "   ⚠️ Configuração não identificada claramente"
    USE_VOLUMES=true
    NGINX_CONF_PATH="nginx.conf"
fi

# 3. Criar nginx.conf correto (HTTP apenas por enquanto)
echo ""
echo "3. Criando nginx.conf correto..."
cat > "$NGINX_CONF_PATH" << 'EOF'
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
    proxy_pass http://app:8080/api/;
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
echo "   ✅ nginx.conf criado/atualizado"

# 4. Aplicar configuração
echo ""
echo "4. Aplicando configuração..."

if [ "$USE_VOLUMES" = true ]; then
    echo "   Usando volumes - reiniciando container..."
    sudo docker-compose -f "$DOCKER_COMPOSE" restart web 2>/dev/null || sudo docker restart geo7-web
    sleep 3
    
    # Copiar nginx.conf diretamente para o container
    echo "   Copiando nginx.conf para o container..."
    sudo docker cp "$NGINX_CONF_PATH" geo7-web:/etc/nginx/conf.d/default.conf
    sudo docker exec geo7-web nginx -s reload
    echo "   ✅ Configuração aplicada"
else
    echo "   Usando build - reconstruindo container..."
    sudo docker-compose -f "$DOCKER_COMPOSE" build web
    sudo docker-compose -f "$DOCKER_COMPOSE" up -d web
    echo "   ✅ Container reconstruído"
fi

# 5. Verificar configuração aplicada
echo ""
echo "5. Verificando configuração aplicada..."
sleep 2
NGINX_CONFIG=$(sudo docker exec geo7-web cat /etc/nginx/conf.d/default.conf 2>/dev/null)
if [ -n "$NGINX_CONFIG" ]; then
    if echo "$NGINX_CONFIG" | grep -q "proxy_pass.*app:8080/api/"; then
        echo "   ✅ nginx.conf correto aplicado"
        echo ""
        echo "   Configuração do proxy:"
        echo "$NGINX_CONFIG" | grep -A 3 "location /api/"
    else
        echo "   ⚠️ nginx.conf pode não estar correto"
    fi
else
    echo "   ⚠️ Não foi possível ler nginx.conf do container"
fi

# 6. Testar
echo ""
echo "6. Testando..."
sleep 2
LOCAL_TEST=$(curl -s -o /dev/null -w "%{http_code}" http://localhost/ 2>/dev/null)
API_TEST=$(curl -s -o /dev/null -w "%{http_code}" http://localhost/api/healthz 2>/dev/null)

echo "   HTTP local (porta 80): $LOCAL_TEST"
echo "   API local (/api/healthz): $API_TEST"

if [ "$LOCAL_TEST" = "200" ] && [ "$API_TEST" = "200" ]; then
    echo "   ✅ Tudo funcionando localmente"
else
    echo "   ⚠️ Algum problema detectado"
fi

# 7. Verificar porta 443
echo ""
echo "7. Verificando porta 443..."
# Desabilitar HTTPS temporariamente se não estiver configurado
if echo "$NGINX_CONFIG" | grep -q "listen 443"; then
    echo "   ⚠️ Nginx está configurado para HTTPS, mas pode não ter certificados"
    echo "   Por enquanto, apenas HTTP (porta 80) está funcionando"
else
    echo "   ✅ Nginx configurado apenas para HTTP (porta 80)"
fi

# Resumo
echo ""
echo "=== CORREÇÃO CONCLUÍDA ==="
echo ""
echo "✅ Nginx configurado para HTTP (porta 80)"
echo ""
echo "⚠️ IMPORTANTE:"
echo "   - Use HTTP (não HTTPS) para acessar: http://18-228-94-6.sslip.io"
echo "   - Limpe o cache do navegador (Ctrl+Shift+R)"
echo "   - Ou use modo anônimo/privado"
echo ""
echo "Para testar:"
echo "   curl http://18-228-94-6.sslip.io"
echo "   curl http://18-228-94-6.sslip.io/api/healthz"
echo ""

