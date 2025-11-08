#!/bin/bash

echo "=== CORRIGINDO NGINX PARA HTTPS NA EC2 ==="
echo ""

# Verificar se docker-compose.yml existe
if [ ! -f "docker-compose.yml" ]; then
    echo "❌ docker-compose.yml não encontrado"
    exit 1
fi

echo "O docker-compose está montando ./nginx.conf que sobrescreve o da imagem."
echo ""
echo "Opções:"
echo "1. Remover o mount e usar o nginx.conf da imagem (recomendado)"
echo "2. Atualizar o nginx.conf local na EC2"
echo ""
read -p "Escolha (1 ou 2): " opcao

if [ "$opcao" = "1" ]; then
    echo ""
    echo "Removendo mount do nginx.conf do docker-compose.yml..."
    
    # Fazer backup
    cp docker-compose.yml docker-compose.yml.backup.$(date +%Y%m%d_%H%M%S)
    
    # Remover a linha do volume nginx.conf
    sed -i '/- \.\/nginx.conf:\/etc\/nginx\/conf.d\/default.conf/d' docker-compose.yml
    
    echo "✅ Mount do nginx.conf removido"
    echo ""
    echo "Agora o nginx.conf da imagem será usado (que já tem HTTPS configurado)"
    echo ""
    echo "Próximos passos:"
    echo "1. Reconstruir a imagem do frontend localmente"
    echo "2. Fazer upload e atualizar na EC2"
    echo "3. Na EC2, executar: sudo docker-compose up -d"
    
elif [ "$opcao" = "2" ]; then
    echo ""
    echo "Atualizando nginx.conf local na EC2..."
    
    # Criar nginx.conf com HTTPS
    cat > nginx.conf << 'EOF'
# Configuração HTTP (porta 80)
server {
  listen 80;
  server_name _;

  root /usr/share/nginx/html;
  index index.html;

  # Arquivos estáticos
  location ~* \.(?:js|css|png|jpg|jpeg|gif|svg|ico|woff2?)$ {
    try_files $uri =404;
    add_header Cache-Control "public, max-age=31536000, immutable";
    access_log off;
  }

  # SPA - Angular
  location / {
    try_files $uri $uri/ /index.html;
  }

  # API - Proxy para backend
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

# Configuração HTTPS (porta 443) - Para produção
server {
  listen 443 ssl http2;
  server_name _;

  # Certificados SSL
  ssl_certificate /etc/ssl/geo7/fullchain.pem;
  ssl_certificate_key /etc/ssl/geo7/privkey.pem;
  
  # Configurações SSL modernas
  ssl_protocols TLSv1.2 TLSv1.3;
  ssl_ciphers ECDHE-RSA-AES256-GCM-SHA512:DHE-RSA-AES256-GCM-SHA512:ECDHE-RSA-AES256-GCM-SHA384:DHE-RSA-AES256-GCM-SHA384:ECDHE-RSA-AES256-SHA384;
  ssl_prefer_server_ciphers off;
  ssl_session_cache shared:SSL:10m;
  ssl_session_timeout 10m;
  
  # Headers de segurança
  add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;
  add_header X-Frame-Options DENY;
  add_header X-Content-Type-Options nosniff;
  add_header X-XSS-Protection "1; mode=block";

  root /usr/share/nginx/html;
  index index.html;

  # Arquivos estáticos
  location ~* \.(?:js|css|png|jpg|jpeg|gif|svg|ico|woff2?)$ {
    try_files $uri =404;
    add_header Cache-Control "public, max-age=31536000, immutable";
    access_log off;
  }

  # SPA - Angular
  location / {
    try_files $uri $uri/ /index.html;
  }

  # API - Proxy para backend
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

    echo "✅ nginx.conf atualizado com HTTPS"
    echo ""
    echo "Agora execute:"
    echo "  sudo docker-compose up -d"
    echo ""
    echo "E teste:"
    echo "  curl -k https://18-228-94-6.sslip.io"
else
    echo "Opção inválida"
    exit 1
fi

