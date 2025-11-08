#!/bin/bash

# Script para prevenir upgrade automático HTTP -> HTTPS
echo "=== PREVENINDO UPGRADE AUTOMÁTICO HTTPS ==="
echo "Data: $(date)"
echo ""

# Criar nginx.conf com headers para prevenir upgrade
cat > /tmp/nginx.conf << 'EOF'
server {
  listen 80;
  server_name _;

  # Prevenir upgrade automático para HTTPS
  add_header Strict-Transport-Security "" always;
  
  root /usr/share/nginx/html;
  index index.html;

  location ~* \.(?:js|css|png|jpg|jpeg|gif|svg|ico|woff2?)$ {
    try_files $uri =404;
    add_header Cache-Control "public, max-age=31536000, immutable";
    access_log off;
  }

  location / {
    try_files $uri $uri/ /index.html;
    
    # Headers para prevenir upgrade HTTPS
    add_header X-Content-Type-Options nosniff always;
    add_header X-Frame-Options DENY always;
  }

  location /api/ {
    proxy_pass http://app:8080/api/;
    proxy_http_version 1.1;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto http;  # Forçar HTTP
    proxy_buffering off;
    proxy_redirect off;
    
    # Headers CORS
    add_header Access-Control-Allow-Origin * always;
    add_header Access-Control-Allow-Methods "GET, POST, PUT, DELETE, OPTIONS" always;
    add_header Access-Control-Allow-Headers "Authorization, Content-Type" always;
    
    # Tratar OPTIONS
    if ($request_method = 'OPTIONS') {
      return 204;
    }
  }
}
EOF

# Aplicar configuração
echo "Aplicando configuração..."
sudo docker cp /tmp/nginx.conf geo7-web:/etc/nginx/conf.d/default.conf
sudo docker exec geo7-web nginx -t
sudo docker exec geo7-web nginx -s reload

echo ""
echo "✅ Configuração aplicada!"
echo ""
echo "⚠️ IMPORTANTE:"
echo "1. Limpe o cache do navegador COMPLETAMENTE:"
echo "   - Chrome: Ctrl+Shift+Delete -> Marque 'Cookies' e 'Imagens e arquivos em cache'"
echo "   - Ou: chrome://net-internals/#hsts -> Delete domain: 18-228-94-6.sslip.io"
echo ""
echo "2. Feche TODAS as abas do navegador"
echo ""
echo "3. Abra uma NOVA janela anônima/privada"
echo ""
echo "4. Acesse: http://18-228-94-6.sslip.io/login"
echo ""
echo "5. Se ainda tentar HTTPS, verifique no DevTools (F12) -> Network"
echo "   e veja qual URL está sendo chamada"

