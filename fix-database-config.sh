#!/bin/bash

echo "=== CORRIGINDO CONFIGURAÇÃO DO BANCO DE DADOS ==="

# 1. Parar containers
echo "1. Parando containers..."
sudo docker-compose down

# 2. Atualizar docker-compose.yml para usar geo7_new
echo "2. Atualizando docker-compose.yml..."
cat > docker-compose.yml << 'EOF'
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

# 3. Remover volume antigo
echo "3. Removendo volume antigo..."
sudo docker volume rm ubuntu_postgres_data 2>/dev/null || true

# 4. Iniciar containers
echo "4. Iniciando containers..."
sudo docker-compose up -d

# 5. Aguardar PostgreSQL inicializar
echo "5. Aguardando PostgreSQL inicializar..."
sleep 30

# 6. Fazer restore dos dados
echo "6. Fazendo restore dos dados..."
sudo docker cp geo7_complete_backup.dump geo7-postgres:/tmp/
sudo docker exec -i geo7-postgres pg_restore -U postgres -d geo7_new /tmp/geo7_complete_backup.dump --clean --if-exists

echo "✅ Configuração do banco corrigida!"
echo "✅ Dados restaurados no banco geo7_new!"
