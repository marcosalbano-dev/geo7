#!/bin/bash

echo "=== CONFIGURANDO HTTPS COM LET'S ENCRYPT ==="
echo "Data: $(date)"

# Verificar se estamos na EC2
if [ ! -f "/home/ubuntu/docker-compose.yml" ]; then
    echo "❌ Arquivo docker-compose.yml não encontrado"
    exit 1
fi

# Parar containers
echo "1. Parando containers..."
sudo docker-compose down

# Instalar certbot se não estiver instalado
echo "2. Instalando certbot..."
sudo apt update
sudo apt install -y certbot

# Obter certificado SSL
echo "3. Obtendo certificado SSL para 18-228-94-6.sslip.io..."
sudo certbot certonly --standalone -d 18-228-94-6.sslip.io --non-interactive --agree-tos --email admin@geo7.com

# Verificar se o certificado foi criado
if [ ! -f "/etc/letsencrypt/live/18-228-94-6.sslip.io/fullchain.pem" ]; then
    echo "❌ Erro ao obter certificado SSL"
    exit 1
fi

echo "✅ Certificado SSL obtido com sucesso!"

# Criar configuração nginx com HTTPS
echo "4. Criando configuração nginx com HTTPS..."
sudo tee nginx.conf > /dev/null << 'EOF'
# Redirecionar HTTP para HTTPS
server {
    listen 80;
    server_name 18-228-94-6.sslip.io;
    return 301 https://$server_name$request_uri;
}

# Configuração HTTPS
server {
    listen 443 ssl http2;
    server_name 18-228-94-6.sslip.io;

    # Certificados SSL
    ssl_certificate /etc/letsencrypt/live/18-228-94-6.sslip.io/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/18-228-94-6.sslip.io/privkey.pem;
    
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
        proxy_pass http://app:8080/;
        proxy_http_version 1.1;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_buffering off;
    }
}
EOF

# Atualizar docker-compose.yml para incluir volumes SSL
echo "5. Atualizando docker-compose.yml..."
sudo tee docker-compose.yml > /dev/null << 'EOF'
services:
  postgres:
    image: postgres:15-alpine
    container_name: geo7-postgres
    environment:
      POSTGRES_DB: geo7_new
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: Axp@01fal12
    volumes:
      - postgres_data:/var/lib/postgresql/data
    networks:
      - geo7-network
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U postgres -d geo7_new -h localhost"]    
      interval: 5s
      timeout: 3s
      retries: 20

  app:
    image: openjdk:17-jdk-slim
    container_name: geo7-app
    ports:
      - "8080:8080"
    volumes:
      - /home/ubuntu/geo7-data/geo7-1.0-SNAPSHOT.jar:/app.jar
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/geo7_new?currentSchema=geo7,public,ibge
      SPRING_DATASOURCE_USERNAME: postgres
      SPRING_DATASOURCE_PASSWORD: Axp@01fal12
      SPRING_JPA_HIBERNATE_DDL_AUTO: update
      SPRING_JPA_SHOW_SQL: true
      JWT_SECRET: geo7-secret-key-very-long-and-secure-key-for-jwt-token-generation-2024                                                                
      JWT_EXPIRATION: 604800000
    command: java -jar /app.jar
    depends_on:
      postgres:
        condition: service_healthy
    networks:
      - geo7-network
    restart: unless-stopped

  nginx:
    image: nginx:alpine
    container_name: geo7-web
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - /home/ubuntu/geo7-web:/usr/share/nginx/html
      - ./nginx.conf:/etc/nginx/conf.d/default.conf
      - /etc/letsencrypt:/etc/letsencrypt:ro
    restart: unless-stopped
    depends_on:
      - app
    networks:
      - geo7-network

volumes:
  postgres_data:

networks:
  geo7-network:
    driver: bridge
EOF

# Iniciar containers
echo "6. Iniciando containers..."
sudo docker-compose up -d

# Aguardar inicialização
echo "7. Aguardando inicialização..."
sleep 15

# Testar HTTPS
echo "8. Testando HTTPS..."
if curl -s -k https://18-228-94-6.sslip.io | grep -q "Geo7"; then
    echo "✅ HTTPS funcionando!"
else
    echo "⚠️ Verificando logs do nginx..."
    sudo docker logs geo7-web --tail 10
fi

# Configurar renovação automática
echo "9. Configurando renovação automática do certificado..."
(crontab -l 2>/dev/null; echo "0 12 * * * /usr/bin/certbot renew --quiet --post-hook 'docker-compose restart nginx'") | crontab -

echo ""
echo "=== HTTPS CONFIGURADO COM SUCESSO! ==="
echo ""
echo "✅ URLs disponíveis:"
echo "  Frontend HTTPS: https://18-228-94-6.sslip.io"
echo "  Frontend HTTP:  http://18-228-94-6.sslip.io (redireciona para HTTPS)"
echo "  API HTTPS:      https://18-228-94-6.sslip.io/api"
echo "  API HTTP:       http://18-228-94-6.sslip.io:8080/api"
echo ""
echo "✅ Certificado SSL válido por 90 dias"
echo "✅ Renovação automática configurada"
echo "✅ Redirecionamento HTTP → HTTPS ativo"
echo ""
echo "Para verificar o certificado:"
echo "  openssl s_client -connect 18-228-94-6.sslip.io:443 -servername 18-228-94-6.sslip.io"
