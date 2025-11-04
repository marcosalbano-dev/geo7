#!/bin/bash

echo "=== CORRIGINDO DOCKER-COMPOSE NA EC2 ==="
echo "Data: $(date)"

# 1. Parar containers atuais
echo ""
echo "1. Parando containers atuais..."
sudo docker-compose down

# 2. Criar o docker-compose correto
echo ""
echo "2. Criando docker-compose correto..."
cat > docker-compose.yml << 'EOF'
services:
  postgres:
    image: postgres:15-alpine
    container_name: geo7-postgres
    environment:
      POSTGRES_DB: geo7
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: Axp@01fal12
    volumes:
      - postgres_data:/var/lib/postgresql/data
    networks:
      - geo7-network
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U postgres -d geo7 -h localhost"]
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
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/geo7
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
      - ./nginx.conf:/etc/nginx/nginx.conf
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

echo "✅ Docker-compose criado"

# 3. Criar nginx.conf se não existir
echo ""
echo "3. Verificando nginx.conf..."
if [ ! -f "nginx.conf" ]; then
    cat > nginx.conf << 'EOF'
events {
    worker_connections 1024;
}

http {
    include       /etc/nginx/mime.types;
    default_type  application/octet-stream;
    
    sendfile        on;
    keepalive_timeout  65;
    
    server {
        listen 80;
        listen 443 ssl;
        server_name _;
        
        root /usr/share/nginx/html;
        index index.html;
        
        # Configuração SSL básica (você pode adicionar certificados depois)
        ssl_certificate /etc/ssl/certs/ssl-cert-snakeoil.pem;
        ssl_certificate_key /etc/ssl/private/ssl-cert-snakeoil.key;
        
        # Proxy para API
        location /api/ {
            proxy_pass http://app:8080;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
        }
        
        # Proxy para auth
        location /auth/ {
            proxy_pass http://app:8080;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
        }
        
        # Servir arquivos estáticos
        location / {
            try_files $uri $uri/ /index.html;
        }
    }
}
EOF
    echo "✅ nginx.conf criado"
else
    echo "✅ nginx.conf já existe"
fi

# 4. Iniciar containers
echo ""
echo "4. Iniciando containers..."
sudo docker-compose up -d

if [ $? -eq 0 ]; then
    echo "✅ Containers iniciados com sucesso"
else
    echo "❌ Erro ao iniciar containers"
    exit 1
fi

# 5. Aguardar inicialização
echo ""
echo "5. Aguardando inicialização..."
sleep 30

# 6. Verificar status
echo ""
echo "6. Verificando status dos containers..."
sudo docker ps

# 7. Verificar logs do PostgreSQL
echo ""
echo "7. Verificando logs do PostgreSQL..."
sudo docker logs geo7-postgres --tail 10

# 8. Verificar logs do App
echo ""
echo "8. Verificando logs do App..."
sudo docker logs geo7-app --tail 10

# 9. Testar health check
echo ""
echo "9. Testando health check..."
for i in {1..5}; do
    if curl -s http://localhost:8080/api/healthz > /dev/null; then
        echo "✅ Backend funcionando"
        break
    else
        echo "   Tentativa $i/5 - Aguardando backend..."
        sleep 10
    fi
done

echo ""
echo "=== CORREÇÃO CONCLUÍDA ==="
echo ""
echo "✅ Docker-compose atualizado com PostgreSQL"
echo "✅ Containers iniciados"
echo ""
echo "URLs para teste:"
echo "  Frontend: http://18-228-94-6.sslip.io"
echo "  API: http://18-228-94-6.sslip.io:8080/api/healthz"
echo ""
echo "Para verificar logs:"
echo "  sudo docker logs geo7-postgres"
echo "  sudo docker logs geo7-app"
echo "  sudo docker logs geo7-web"
