# Script para corrigir a configuração do Angular
Write-Host "=== Corrigindo configuração do Angular ===" -ForegroundColor Green

# 1. Verificar configuração atual
Write-Host "`n1. Verificando configuração atual do Angular..." -ForegroundColor Yellow
ssh ubuntu@18-228-94-6.sslip.io "cat /home/ubuntu/geo7-app/src/environments/environment.prod.ts"

# 2. Verificar se a aplicação Angular está fazendo requisições corretas
Write-Host "`n2. Verificando se o problema é na URL base..." -ForegroundColor Yellow

# A configuração atual deve ser:
# apiUrl: 'http://18-228-94-6.sslip.io' (sem /api)
# E a aplicação deve fazer requisições para:
# - http://18-228-94-6.sslip.io/api/municipios
# - http://18-228-94-6.sslip.io/api/lotes

# 3. Verificar se o Caddy está configurado corretamente
Write-Host "`n3. Verificando configuração do Caddy..." -ForegroundColor Yellow
ssh ubuntu@18-228-94-6.sslip.io "docker exec geo7-caddy-1 cat /etc/caddy/Caddyfile"

Write-Host "`n=== Análise concluída ===" -ForegroundColor Green
Write-Host "O problema pode estar em:" -ForegroundColor Yellow
Write-Host "1. A aplicação Angular não está usando o prefixo /api nas requisições" -ForegroundColor Cyan
Write-Host "2. O Caddy não está configurado para /api/municipios e /api/lotes" -ForegroundColor Cyan
Write-Host "3. A aplicação Angular está fazendo requisições para URLs incorretas" -ForegroundColor Cyan
