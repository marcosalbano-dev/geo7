# Script para atualizar o Caddyfile com as rotas corretas
Write-Host "=== Atualizando Caddyfile ===" -ForegroundColor Green

$caddyfileContent = @"
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
        # Remove Origin header to avoid CORS issues
        header -Origin
        
        # Add CORS headers
        header Access-Control-Allow-Origin "http://18-228-94-6.sslip.io:8081"
        header Access-Control-Allow-Methods "GET, POST, PUT, PATCH, DELETE, OPTIONS"
        header Access-Control-Allow-Headers "Content-Type, Authorization, X-Requested-With"
        header Access-Control-Allow-Credentials "true"
        header Access-Control-Expose-Headers "Authorization, Content-Type"
        
        # Handle OPTIONS requests
        @options {
            method OPTIONS
        }
        respond @options 200
        
        # Proxy to API
        reverse_proxy api:8080 {
            header_up -Origin
        }
    }

    # API endpoints with CORS (with /api prefix)
    handle /api/* {
        # Remove Origin header to avoid CORS issues
        header -Origin
        
        # Add CORS headers
        header Access-Control-Allow-Origin "http://18-228-94-6.sslip.io:8081"
        header Access-Control-Allow-Methods "GET, POST, PUT, PATCH, DELETE, OPTIONS"
        header Access-Control-Allow-Headers "Content-Type, Authorization, X-Requested-With"
        header Access-Control-Allow-Credentials "true"
        header Access-Control-Expose-Headers "Authorization, Content-Type"
        
        # Handle OPTIONS requests
        @options {
            method OPTIONS
        }
        respond @options 200
        
        # Proxy to API
        reverse_proxy api:8080 {
            header_up -Origin
        }
    }

    # Direct API routes (without /api prefix) - for Angular compatibility
    handle /municipios* {
        # Remove Origin header to avoid CORS issues
        header -Origin
        
        # Add CORS headers
        header Access-Control-Allow-Origin "http://18-228-94-6.sslip.io:8081"
        header Access-Control-Allow-Methods "GET, POST, PUT, PATCH, DELETE, OPTIONS"
        header Access-Control-Allow-Headers "Content-Type, Authorization, X-Requested-With"
        header Access-Control-Allow-Credentials "true"
        header Access-Control-Expose-Headers "Authorization, Content-Type"
        
        # Handle OPTIONS requests
        @options {
            method OPTIONS
        }
        respond @options 200
        
        # Proxy to API with /api prefix
        reverse_proxy api:8080/api/municipios {
            header_up -Origin
        }
    }

    handle /lotes* {
        # Remove Origin header to avoid CORS issues
        header -Origin
        
        # Add CORS headers
        header Access-Control-Allow-Origin "http://18-228-94-6.sslip.io:8081"
        header Access-Control-Allow-Methods "GET, POST, PUT, PATCH, DELETE, OPTIONS"
        header Access-Control-Allow-Headers "Content-Type, Authorization, X-Requested-With"
        header Access-Control-Allow-Credentials "true"
        header Access-Control-Expose-Headers "Authorization, Content-Type"
        
        # Handle OPTIONS requests
        @options {
            method OPTIONS
        }
        respond @options 200
        
        # Proxy to API with /api prefix
        reverse_proxy api:8080/api/lotes {
            header_up -Origin
        }
    }

    handle /pessoas* {
        # Remove Origin header to avoid CORS issues
        header -Origin
        
        # Add CORS headers
        header Access-Control-Allow-Origin "http://18-228-94-6.sslip.io:8081"
        header Access-Control-Allow-Methods "GET, POST, PUT, PATCH, DELETE, OPTIONS"
        header Access-Control-Allow-Headers "Content-Type, Authorization, X-Requested-With"
        header Access-Control-Allow-Credentials "true"
        header Access-Control-Expose-Headers "Authorization, Content-Type"
        
        # Handle OPTIONS requests
        @options {
            method OPTIONS
        }
        respond @options 200
        
        # Proxy to API with /api prefix
        reverse_proxy api:8080/api/pessoas {
            header_up -Origin
        }
    }

    handle /estruturas* {
        # Remove Origin header to avoid CORS issues
        header -Origin
        
        # Add CORS headers
        header Access-Control-Allow-Origin "http://18-228-94-6.sslip.io:8081"
        header Access-Control-Allow-Methods "GET, POST, PUT, PATCH, DELETE, OPTIONS"
        header Access-Control-Allow-Headers "Content-Type, Authorization, X-Requested-With"
        header Access-Control-Allow-Credentials "true"
        header Access-Control-Expose-Headers "Authorization, Content-Type"
        
        # Handle OPTIONS requests
        @options {
            method OPTIONS
        }
        respond @options 200
        
        # Proxy to API with /api prefix
        reverse_proxy api:8080/api/estruturas {
            header_up -Origin
        }
    }

    handle /situacoes-juridicas* {
        # Remove Origin header to avoid CORS issues
        header -Origin
        
        # Add CORS headers
        header Access-Control-Allow-Origin "http://18-228-94-6.sslip.io:8081"
        header Access-Control-Allow-Methods "GET, POST, PUT, PATCH, DELETE, OPTIONS"
        header Access-Control-Allow-Headers "Content-Type, Authorization, X-Requested-With"
        header Access-Control-Allow-Credentials "true"
        header Access-Control-Expose-Headers "Authorization, Content-Type"
        
        # Handle OPTIONS requests
        @options {
            method OPTIONS
        }
        respond @options 200
        
        # Proxy to API with /api prefix
        reverse_proxy api:8080/api/situacoes-juridicas {
            header_up -Origin
        }
    }

    handle /usos* {
        # Remove Origin header to avoid CORS issues
        header -Origin
        
        # Add CORS headers
        header Access-Control-Allow-Origin "http://18-228-94-6.sslip.io:8081"
        header Access-Control-Allow-Methods "GET, POST, PUT, PATCH, DELETE, OPTIONS"
        header Access-Control-Allow-Headers "Content-Type, Authorization, X-Requested-With"
        header Access-Control-Allow-Credentials "true"
        header Access-Control-Expose-Headers "Authorization, Content-Type"
        
        # Handle OPTIONS requests
        @options {
            method OPTIONS
        }
        respond @options 200
        
        # Proxy to API with /api prefix
        reverse_proxy api:8080/api/usos {
            header_up -Origin
        }
    }
}
"@

Write-Host "`n1. Atualizando Caddyfile..." -ForegroundColor Yellow
ssh ubuntu@18-228-94-6.sslip.io "docker exec geo7-caddy-1 sh -c 'cat > /etc/caddy/Caddyfile << '\''EOF'\''
$caddyfileContent
EOF'"

Write-Host "`n2. Recarregando configuração do Caddy..." -ForegroundColor Yellow
ssh ubuntu@18-228-94-6.sslip.io "docker exec geo7-caddy-1 caddy reload --config /etc/caddy/Caddyfile"

Write-Host "`n3. Testando endpoints..." -ForegroundColor Yellow

# Fazer login
$loginResponse = ssh ubuntu@18-228-94-6.sslip.io "curl -s -X POST http://18-228-94-6.sslip.io:8080/auth/login -H 'Content-Type: application/json' -d '{\"email\":\"admin@geo7.com\",\"password\":\"admin123\"}'"
$token = ($loginResponse | ConvertFrom-Json).token

Write-Host "Token: $($token.Substring(0, 20))..." -ForegroundColor Cyan

# Testar /municipios (sem /api)
Write-Host "`nTestando /municipios (sem /api)..." -ForegroundColor Cyan
ssh ubuntu@18-228-94-6.sslip.io "curl -s -X GET http://18-228-94-6.sslip.io/municipios -H 'Authorization: Bearer $token' -H 'Origin: http://18-228-94-6.sslip.io:8081' | jq '.[0:3]'"

# Testar /lotes (sem /api)
Write-Host "`nTestando /lotes (sem /api)..." -ForegroundColor Cyan
ssh ubuntu@18-228-94-6.sslip.io "curl -s -X GET http://18-228-94-6.sslip.io/lotes -H 'Authorization: Bearer $token' -H 'Origin: http://18-228-94-6.sslip.io:8081' | jq ."

Write-Host "`n=== Caddyfile atualizado ===" -ForegroundColor Green
Write-Host "Agora teste a aplicação Angular para ver se os dados estão carregando." -ForegroundColor Yellow
