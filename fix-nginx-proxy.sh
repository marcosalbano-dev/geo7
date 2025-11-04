#!/bin/bash

# Script para corrigir configuração do proxy Nginx na EC2
echo "=== CORRIGINDO PROXY NGINX NA EC2 ==="
echo "Data: $(date)"

# Verificar se estamos na EC2
if [ ! -f "/etc/nginx/sites-available/default" ]; then
    echo "❌ Nginx não encontrado. Verificando se está usando Docker..."
    
    # Verificar se está usando Docker
    if [ -f "/home/ubuntu/geo7/docker/docker-compose.yml" ]; then
        echo "✅ Encontrado Docker Compose. Corrigindo configuração do container..."
        
        # Parar containers
        echo ""
        echo "1. Parando containers..."
        cd /home/ubuntu/geo7/docker
        sudo docker-compose down
        
        # Corrigir configuração do Nginx no container
        echo ""
        echo "2. Corrigindo configuração do Nginx..."
        
        # Criar arquivo de configuração corrigido
        sudo tee /home/ubuntu/geo7/docker/nginx.conf > /dev/null << 'EOF'
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

    # API - CORRIGIDO: removendo /api do proxy_pass
    location /api/ {
        proxy_pass http://app:8080/;   # <-- CORRIGIDO: barra simples para remover /api
        proxy_http_version 1.1;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_buffering off;
    }
}
EOF
        
        echo "✅ Configuração do Nginx corrigida"
        
        # Rebuildar container web
        echo ""
        echo "3. Rebuildando container web..."
        sudo docker-compose build web
        
        # Iniciar containers
        echo ""
        echo "4. Iniciando containers..."
        sudo docker-compose up -d
        
        # Aguardar inicialização
        echo ""
        echo "5. Aguardando inicialização..."
        sleep 15
        
        # Testar configuração
        echo ""
        echo "6. Testando configuração..."
        if curl -s -X POST http://localhost/api/auth/login -H "Content-Type: application/json" -d '{"email":"admin@geo7.com","password":"admin123"}' | grep -q "Login realizado com sucesso"; then
            echo "✅ Proxy funcionando corretamente!"
        else
            echo "⚠️ Aviso: Proxy pode não estar funcionando. Verificando logs..."
            sudo docker logs geo7-web --tail 10
        fi
        
    else
        echo "❌ Docker Compose não encontrado em /home/ubuntu/geo7/docker/"
        exit 1
    fi
    
else
    echo "✅ Nginx encontrado. Corrigindo configuração..."
    
    # Fazer backup da configuração atual
    echo ""
    echo "1. Fazendo backup da configuração atual..."
    sudo cp /etc/nginx/sites-available/default /etc/nginx/sites-available/default.backup.$(date +%Y%m%d_%H%M%S)
    
    # Corrigir configuração
    echo ""
    echo "2. Corrigindo configuração do Nginx..."
    sudo sed -i 's|proxy_pass http://app:8080/api/;|proxy_pass http://app:8080/;|g' /etc/nginx/sites-available/default
    
    # Testar configuração
    echo ""
    echo "3. Testando configuração..."
    sudo nginx -t
    
    if [ $? -eq 0 ]; then
        echo "✅ Configuração válida"
        
        # Recarregar Nginx
        echo ""
        echo "4. Recarregando Nginx..."
        sudo systemctl reload nginx
        
        # Testar proxy
        echo ""
        echo "5. Testando proxy..."
        if curl -s -X POST http://localhost/api/auth/login -H "Content-Type: application/json" -d '{"email":"admin@geo7.com","password":"admin123"}' | grep -q "Login realizado com sucesso"; then
            echo "✅ Proxy funcionando corretamente!"
        else
            echo "⚠️ Aviso: Proxy pode não estar funcionando"
        fi
    else
        echo "❌ Configuração inválida. Restaurando backup..."
        sudo cp /etc/nginx/sites-available/default.backup.$(date +%Y%m%d_%H%M%S) /etc/nginx/sites-available/default
        exit 1
    fi
fi

echo ""
echo "=== CORREÇÃO CONCLUÍDA ==="
echo ""
echo "✅ Proxy Nginx corrigido!"
echo ""
echo "URLs para teste:"
echo "  Frontend: https://18-228-94-6.sslip.io"
echo "  Login: https://18-228-94-6.sslip.io/login"
echo ""
echo "Para verificar logs:"
echo "  sudo docker logs geo7-web"
echo "  sudo docker logs geo7-app"
