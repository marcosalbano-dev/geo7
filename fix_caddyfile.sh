#!/bin/bash

echo "=== Atualizando Caddyfile ==="

# Atualizar Caddyfile
docker exec geo7-caddy-1 sh -c 'cat > /etc/caddy/Caddyfile << '\''EOF'\''
{
    email marcos.telematica@gmail.com
}

18-228-94-6.sslip.io:80 {
    encode gzip

    # Frontend Angular
    handle {
        root * /home/ubuntu/geo7-app/dist/geo7-app
        file_server
        try_files {path} /index.html

        # Headers para evitar cache
        header Cache-Control "no-cache, no-store, must-revalidate"
        header Pragma "no-cache"
        header Expires "0"
    }

    # Auth endpoints with CORS
    handle /auth/* {
        header -Origin
        header Access-Control-Allow-Origin "http://18-228-94-6.sslip.io:8081"
        header Access-Control-Allow-Methods "GET, POST, PUT, PATCH, DELETE, OPTIONS"
        header Access-Control-Allow-Headers "Content-Type, Authorization, X-Requested-With"
        header Access-Control-Allow-Credentials "true"
        header Access-Control-Expose-Headers "Authorization, Content-Type"
        
        @options {
            method OPTIONS
        }
        respond @options 200
        
        reverse_proxy api:8080 {
            header_up -Origin
        }
    }

    # API endpoints with CORS (with /api prefix)
    handle /api/* {
        header -Origin
        header Access-Control-Allow-Origin "http://18-228-94-6.sslip.io:8081"
        header Access-Control-Allow-Methods "GET, POST, PUT, PATCH, DELETE, OPTIONS"
        header Access-Control-Allow-Headers "Content-Type, Authorization, X-Requested-With"
        header Access-Control-Allow-Credentials "true"
        header Access-Control-Expose-Headers "Authorization, Content-Type"
        
        @options {
            method OPTIONS
        }
        respond @options 200
        
        reverse_proxy api:8080 {
            header_up -Origin
        }
    }

    # Direct API routes (without /api prefix) - for Angular compatibility
    handle /municipios* {
        header -Origin
        header Access-Control-Allow-Origin "http://18-228-94-6.sslip.io:8081"
        header Access-Control-Allow-Methods "GET, POST, PUT, PATCH, DELETE, OPTIONS"
        header Access-Control-Allow-Headers "Content-Type, Authorization, X-Requested-With"
        header Access-Control-Allow-Credentials "true"
        header Access-Control-Expose-Headers "Authorization, Content-Type"
        
        @options {
            method OPTIONS
        }
        respond @options 200
        
        reverse_proxy api:8080/api/municipios {
            header_up -Origin
        }
    }

    handle /lotes* {
        header -Origin
        header Access-Control-Allow-Origin "http://18-228-94-6.sslip.io:8081"
        header Access-Control-Allow-Methods "GET, POST, PUT, PATCH, DELETE, OPTIONS"
        header Access-Control-Allow-Headers "Content-Type, Authorization, X-Requested-With"
        header Access-Control-Allow-Credentials "true"
        header Access-Control-Expose-Headers "Authorization, Content-Type"
        
        @options {
            method OPTIONS
        }
        respond @options 200
        
        reverse_proxy api:8080/api/lotes {
            header_up -Origin
        }
    }
}
EOF'

echo "Recarregando configuração do Caddy..."
docker exec geo7-caddy-1 caddy reload --config /etc/caddy/Caddyfile

echo "Testando endpoints..."

# Fazer login
LOGIN_RESPONSE=$(curl -s -X POST http://18-228-94-6.sslip.io:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@geo7.com","password":"admin123"}')

TOKEN=$(echo $LOGIN_RESPONSE | jq -r '.token')
echo "Token: ${TOKEN:0:20}..."

# Testar /municipios (sem /api)
echo "Testando /municipios (sem /api)..."
curl -s -X GET http://18-228-94-6.sslip.io/municipios \
  -H "Authorization: Bearer $TOKEN" \
  -H "Origin: http://18-228-94-6.sslip.io:8081" | jq '.[0:3]'

# Testar /lotes (sem /api)
echo "Testando /lotes (sem /api)..."
curl -s -X GET http://18-228-94-6.sslip.io/lotes \
  -H "Authorization: Bearer $TOKEN" \
  -H "Origin: http://18-228-94-6.sslip.io:8081" | jq .

echo "=== Caddyfile atualizado ==="
echo "Agora teste a aplicação Angular para ver se os dados estão carregando."
