#!/bin/bash

# Script para corrigir cache do nginx - adicionar headers no-cache para index.html

echo "=== CORRIGINDO CACHE DO NGINX ==="
echo "Data: $(date)"
echo ""

# Verificar se o nginx.conf existe
if [ ! -f "nginx.conf" ]; then
    echo "⚠️ nginx.conf não encontrado no diretório atual"
    echo "   Verificando se está no diretório correto..."
    exit 1
fi

# Fazer backup
echo "1. Fazendo backup do nginx.conf..."
cp nginx.conf nginx.conf.backup.$(date +%Y%m%d_%H%M%S)
echo "   ✅ Backup criado"

# Atualizar nginx.conf para adicionar headers no-cache para index.html
echo ""
echo "2. Atualizando nginx.conf..."

# Criar novo nginx.conf com headers no-cache para index.html
cat > nginx.conf << 'EOF'
server {
  listen 80;
  server_name _;

  root /usr/share/nginx/html;
  index index.html;

  # index.html - SEMPRE sem cache (força navegador a buscar versão atualizada)
  location = /index.html {
    add_header Cache-Control "no-cache, no-store, must-revalidate, max-age=0";
    add_header Pragma "no-cache";
    add_header Expires "0";
    try_files $uri =404;
  }

  # Arquivos estáticos (JS, CSS, imagens) - cache longo (OK pois têm hash no nome)
  location ~* \.(?:js|css|png|jpg|jpeg|gif|svg|ico|woff2?)$ {
    try_files $uri =404;
    add_header Cache-Control "public, max-age=31536000, immutable";
    access_log off;
  }

  # SPA - Angular (rotas)
  location / {
    try_files $uri $uri/ /index.html;
    # Headers no-cache para garantir que sempre busca versão atualizada
    add_header Cache-Control "no-cache, no-store, must-revalidate, max-age=0";
    add_header Pragma "no-cache";
    add_header Expires "0";
  }

  # API
  location /api/ {
    proxy_pass http://app:8080;
    proxy_http_version 1.1;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto $scheme;
    proxy_buffering off;
  }
}
EOF

echo "   ✅ nginx.conf atualizado com headers no-cache"

# Reiniciar container do nginx
echo ""
echo "3. Reiniciando container do nginx..."
sudo docker-compose down
sudo docker-compose up -d

if [ $? -eq 0 ]; then
    echo "   ✅ Containers reiniciados"
else
    echo "   ❌ Erro ao reiniciar containers"
    exit 1
fi

# Aguardar inicialização
echo ""
echo "4. Aguardando inicialização..."
sleep 10

# Verificar se nginx está funcionando
echo ""
echo "5. Verificando nginx..."
sudo docker exec geo7-web nginx -t 2>&1
if [ $? -eq 0 ]; then
    echo "   ✅ Configuração do nginx válida"
else
    echo "   ⚠️ Aviso: Pode haver problemas na configuração"
fi

echo ""
echo "=== CORREÇÃO CONCLUÍDA ==="
echo ""
echo "✅ Nginx configurado para não fazer cache do index.html"
echo ""
echo "Agora o navegador sempre buscará a versão mais recente do index.html"
echo "Os arquivos JS/CSS continuam com cache (OK pois têm hash no nome)"
echo ""
echo "Teste acessando: https://18-228-94-6.sslip.io"
echo "E limpe o cache do navegador: Ctrl+Shift+R (Windows) ou Cmd+Shift+R (Mac)"
echo ""

