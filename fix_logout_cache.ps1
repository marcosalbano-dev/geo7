# Script para corrigir problema de cache após logout
Write-Host "=== Corrigindo problema de cache após logout ===" -ForegroundColor Green

# Atualizar Caddyfile com headers mais agressivos para evitar cache
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

        # Headers mais agressivos para evitar cache
        header Cache-Control "no-cache, no-store, must-revalidate, max-age=0"
        header Pragma "no-cache"
        header Expires "0"
        header Last-Modified "Thu, 01 Jan 1970 00:00:00 GMT"
        header ETag ""
    }

    # Auth endpoints with CORS
    handle /auth/* {
        header -Origin
        header Access-Control-Allow-Origin "http://18-228-94-6.sslip.io:8081"
        header Access-Control-Allow-Methods "GET, POST, PUT, PATCH, DELETE, OPTIONS"
        header Access-Control-Allow-Headers "Content-Type, Authorization, X-Requested-With"
        header Access-Control-Allow-Credentials "true"
        header Access-Control-Expose-Headers "Authorization, Content-Type"
        
        # Headers para evitar cache
        header Cache-Control "no-cache, no-store, must-revalidate"
        header Pragma "no-cache"
        header Expires "0"
        
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
        
        # Headers para evitar cache
        header Cache-Control "no-cache, no-store, must-revalidate"
        header Pragma "no-cache"
        header Expires "0"
        
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
        
        # Headers para evitar cache
        header Cache-Control "no-cache, no-store, must-revalidate"
        header Pragma "no-cache"
        header Expires "0"
        
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
        
        # Headers para evitar cache
        header Cache-Control "no-cache, no-store, must-revalidate"
        header Pragma "no-cache"
        header Expires "0"
        
        @options {
            method OPTIONS
        }
        respond @options 200
        
        reverse_proxy api:8080/api/lotes {
            header_up -Origin
        }
    }
}
"@

Write-Host "`n1. Atualizando Caddyfile com headers anti-cache..." -ForegroundColor Yellow
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

# Testar /municipios
Write-Host "`nTestando /municipios..." -ForegroundColor Cyan
ssh ubuntu@18-228-94-6.sslip.io "curl -s -X GET http://18-228-94-6.sslip.io/municipios -H 'Authorization: Bearer $token' -H 'Origin: http://18-228-94-6.sslip.io:8081' | jq '.[0:2]'"

# Testar /lotes
Write-Host "`nTestando /lotes..." -ForegroundColor Cyan
ssh ubuntu@18-228-94-6.sslip.io "curl -s -X GET http://18-228-94-6.sslip.io/lotes -H 'Authorization: Bearer $token' -H 'Origin: http://18-228-94-6.sslip.io:8081' | jq ."

Write-Host "`n=== Caddyfile atualizado com headers anti-cache ===" -ForegroundColor Green
Write-Host "Agora teste a aplicação Angular:" -ForegroundColor Yellow
Write-Host "1. Acesse http://18-228-94-6.sslip.io:8081" -ForegroundColor Cyan
Write-Host "2. Faça login com admin@geo7.com / admin123" -ForegroundColor Cyan
Write-Host "3. Verifique se os dados carregam" -ForegroundColor Cyan
Write-Host "4. Faça logout" -ForegroundColor Cyan
Write-Host "5. Faça login novamente e verifique se os dados carregam" -ForegroundColor Cyan
Write-Host "6. Se ainda não funcionar, limpe o cache do navegador (Ctrl+Shift+R)" -ForegroundColor Cyan
