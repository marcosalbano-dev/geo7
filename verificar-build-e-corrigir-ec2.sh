#!/bin/bash

# Script para verificar build e corrigir problema HTTPS
echo "=== VERIFICANDO BUILD E CORRIGINDO ==="
echo "Data: $(date)"
echo ""

# 1. Verificar build atual
echo "1. Verificando build do frontend..."
MAIN_JS=$(find /var/www/html -name "main*.js" -type f 2>/dev/null | head -1)
if [ -n "$MAIN_JS" ]; then
    echo "   Arquivo: $MAIN_JS"
    echo ""
    echo "   Verificando URLs no build..."
    
    # Verificar se contém URLs absolutas HTTPS
    if grep -q "https://18-228-94-6.sslip.io" "$MAIN_JS" 2>/dev/null; then
        echo "   ❌ PROBLEMA ENCONTRADO: Build contém URLs absolutas HTTPS!"
        echo "   Isso faz o navegador tentar HTTPS mesmo quando acessa via HTTP"
        BUILD_PROBLEMA=true
    elif grep -q "http://18-228-94-6.sslip.io" "$MAIN_JS" 2>/dev/null; then
        echo "   ⚠️ Build contém URLs absolutas HTTP"
        BUILD_PROBLEMA=true
    else
        echo "   ✅ Build parece usar URLs relativas"
        BUILD_PROBLEMA=false
    fi
    
    # Verificar se contém '/api' (URL relativa)
    echo ""
    echo "   Verificando se contém '/api' (URL relativa)..."
    if grep -q '"/api' "$MAIN_JS" 2>/dev/null || grep -q "'/api" "$MAIN_JS" 2>/dev/null; then
        echo "   ✅ Build contém '/api' (URL relativa encontrada)"
    else
        echo "   ⚠️ URL relativa '/api' não encontrada"
        BUILD_PROBLEMA=true
    fi
else
    echo "   ❌ Arquivo main.js não encontrado!"
    BUILD_PROBLEMA=true
fi

# 2. Corrigir nginx.conf
echo ""
echo "2. Corrigindo nginx.conf..."
cat > /tmp/nginx.conf << 'EOF'
server {
  listen 80;
  server_name _;

  root /usr/share/nginx/html;
  index index.html;

  location ~* \.(?:js|css|png|jpg|jpeg|gif|svg|ico|woff2?)$ {
    try_files $uri =404;
    add_header Cache-Control "public, max-age=31536000, immutable";
    access_log off;
  }

  location / {
    try_files $uri $uri/ /index.html;
  }

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

sudo docker cp /tmp/nginx.conf geo7-web:/etc/nginx/conf.d/default.conf
sudo docker exec geo7-web nginx -t
sudo docker exec geo7-web nginx -s reload
echo "   ✅ nginx.conf corrigido e recarregado"

# 3. Resumo
echo ""
echo "=== RESUMO ==="
echo ""
if [ "$BUILD_PROBLEMA" = true ]; then
    echo "❌ PROBLEMA: Build do frontend contém URLs absolutas"
    echo ""
    echo "SOLUÇÃO:"
    echo "1. No seu computador local, execute:"
    echo "   cd geo7-app"
    echo "   npm run build:prod"
    echo ""
    echo "2. Execute:"
    echo "   .\atualizar-local-completo.ps1"
    echo "   .\upload-ec2.ps1"
    echo ""
    echo "3. Na EC2, execute:"
    echo "   ./atualizar-ec2.sh"
else
    echo "✅ Build parece correto"
    echo ""
    echo "O problema pode ser:"
    echo "1. Cache do navegador (HSTS)"
    echo "2. Nginx não está aplicando a configuração correta"
    echo ""
    echo "Teste agora:"
    echo "  - Limpe o cache do navegador (Ctrl+Shift+Delete)"
    echo "  - Ou use modo anônimo"
    echo "  - Acesse: http://18-228-94-6.sslip.io/login"
fi

echo ""
echo "✅ Verificação concluída!"

