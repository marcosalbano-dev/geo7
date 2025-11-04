#!/bin/bash

# Script para configurar a EC2 inicialmente
echo "=== CONFIGURANDO EC2 PARA GEO7 ==="

# 1. Atualizar sistema
echo "1. Atualizando sistema..."
sudo apt update && sudo apt upgrade -y

# 2. Instalar Docker
echo "2. Instalando Docker..."
sudo apt install -y docker.io docker-compose

# 3. Adicionar usuário ao grupo docker
echo "3. Configurando permissões Docker..."
sudo usermod -aG docker ubuntu

# 4. Iniciar Docker
echo "4. Iniciando Docker..."
sudo systemctl start docker
sudo systemctl enable docker

# 5. Criar diretórios necessários
echo "5. Criando diretórios..."
sudo mkdir -p /opt/geo7
sudo mkdir -p /var/www/html
sudo mkdir -p /opt/backup

# 6. Configurar permissões
echo "6. Configurando permissões..."
sudo chown -R ubuntu:ubuntu /opt/geo7
sudo chown -R www-data:www-data /var/www/html
sudo chmod -R 755 /var/www/html

# 7. Criar docker-compose.yml
echo "7. Criando docker-compose.yml..."
cat > docker-compose.yml << 'EOF'
version: '3.8'
services:
  app:
    image: openjdk:17-jre-slim
    container_name: geo7-app
    ports:
      - "8080:8080"
    volumes:
      - /opt/geo7/geo7-1.0-SNAPSHOT.jar:/app.jar
    command: java -jar /app.jar
    restart: unless-stopped

  nginx:
    image: nginx:alpine
    container_name: geo7-web
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - /var/www/html:/usr/share/nginx/html
      - ./nginx.conf:/etc/nginx/nginx.conf
    restart: unless-stopped
    depends_on:
      - app
EOF

# 8. Criar nginx.conf
echo "8. Criando nginx.conf..."
cat > nginx.conf << 'EOF'
events {
    worker_connections 1024;
}

http {
    include /etc/nginx/mime.types;
    default_type application/octet-stream;

    server {
        listen 80;
        server_name 18-228-94-6.sslip.io;

        # Frontend
        location / {
            root /usr/share/nginx/html;
            index index.html;
            try_files $uri $uri/ /index.html;
        }

        # API Backend
        location /api/ {
            proxy_pass http://app:8080/;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
        }
    }
}
EOF

echo "✅ Configuração inicial concluída!"
echo ""
echo "Próximos passos:"
echo "1. Reinicie a sessão SSH para aplicar as permissões Docker"
echo "2. Execute: ./atualizar-ec2.sh"
