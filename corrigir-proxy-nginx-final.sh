#!/bin/bash

# Script para corrigir proxy do nginx - VERSÃO FINAL
echo "=== CORRIGINDO PROXY NGINX (VERSÃO FINAL) ==="
echo "Data: $(date)"
echo ""

# 1. Criar nginx.conf correto
echo "1. Criando nginx.conf correto..."
cat > /tmp/nginx.conf << 'EOF'
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

  # API - CORRIGIDO: com /api/ no final
  location /api/ {
    proxy_pass http://app:8080/api/;
    proxy_http_version 1.1;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto $scheme;
    proxy_buffering off;
    proxy_redirect off;
    
    # Headers CORS (se necessário)
    add_header Access-Control-Allow-Origin * always;
    add_header Access-Control-Allow-Methods "GET, POST, PUT, DELETE, OPTIONS" always;
    add_header Access-Control-Allow-Headers "Authorization, Content-Type" always;
    
    # Tratar OPTIONS para CORS
    if ($request_method = 'OPTIONS') {
      return 204;
    }
  }
}
EOF

# 2. Copiar para o container
echo ""
echo "2. Copiando nginx.conf para o container..."
sudo docker cp /tmp/nginx.conf geo7-web:/etc/nginx/conf.d/default.conf

# 3. Testar configuração
echo ""
echo "3. Testando configuração do nginx..."
sudo docker exec geo7-web nginx -t

# 4. Recarregar nginx
echo ""
echo "4. Recarregando nginx..."
sudo docker exec geo7-web nginx -s reload

# 5. Verificar configuração aplicada
echo ""
echo "5. Verificando configuração aplicada..."
sudo docker exec geo7-web cat /etc/nginx/conf.d/default.conf | grep -A 8 "location /api/"

# 6. Testar
echo ""
echo "6. Testando proxy..."
sleep 2
echo "   Testando GET:"
curl -s http://localhost/api/healthz
echo ""
echo ""
echo "   Testando POST (simulação de login):"
curl -s -X POST http://localhost/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@test.com","password":"test"}' | head -1
echo ""

# 7. Verificar logs
echo ""
echo "7. Verificando logs recentes..."
sudo docker logs geo7-web --tail 5

echo ""
echo "=== CORREÇÃO CONCLUÍDA ==="
echo ""
echo "✅ Proxy corrigido!"
echo ""
echo "⚠️ IMPORTANTE:"
echo "   1. Limpe o cache do navegador (Ctrl+Shift+Delete)"
echo "   2. Ou use modo anônimo/privado"
echo "   3. Teste: http://18-228-94-6.sslip.io/login"
echo ""

