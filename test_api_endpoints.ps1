# Script para testar os endpoints da API
Write-Host "=== Testando endpoints da API ===" -ForegroundColor Green

# 1. Fazer login para obter token
Write-Host "`n1. Fazendo login..." -ForegroundColor Yellow
$loginResponse = ssh ubuntu@18-228-94-6.sslip.io "curl -s -X POST http://18-228-94-6.sslip.io:8080/auth/login -H 'Content-Type: application/json' -d '{\"email\":\"admin@geo7.com\",\"password\":\"admin123\"}'"
$loginData = $loginResponse | ConvertFrom-Json
$token = $loginData.token

if ($token) {
    Write-Host "✓ Login realizado com sucesso" -ForegroundColor Green
    Write-Host "Token: $($token.Substring(0, 20))..." -ForegroundColor Cyan
} else {
    Write-Host "✗ Erro no login" -ForegroundColor Red
    Write-Host "Resposta: $loginResponse" -ForegroundColor Red
    exit 1
}

# 2. Testar endpoint de municípios
Write-Host "`n2. Testando /api/municipios..." -ForegroundColor Yellow
$municipiosResponse = ssh ubuntu@18-228-94-6.sslip.io "curl -s -X GET http://18-228-94-6.sslip.io:8080/api/municipios -H 'Authorization: Bearer $token'"
Write-Host "Resposta: $municipiosResponse" -ForegroundColor Cyan

# 3. Testar endpoint de lotes
Write-Host "`n3. Testando /api/lotes..." -ForegroundColor Yellow
$lotesResponse = ssh ubuntu@18-228-94-6.sslip.io "curl -s -X GET http://18-228-94-6.sslip.io:8080/api/lotes -H 'Authorization: Bearer $token'"
Write-Host "Resposta: $lotesResponse" -ForegroundColor Cyan

# 4. Testar através do Caddy (proxy)
Write-Host "`n4. Testando através do Caddy (proxy)..." -ForegroundColor Yellow

# Testar municípios via Caddy
Write-Host "Testando /api/municipios via Caddy..." -ForegroundColor Cyan
$municipiosCaddyResponse = ssh ubuntu@18-228-94-6.sslip.io "curl -s -X GET http://18-228-94-6.sslip.io/api/municipios -H 'Authorization: Bearer $token' -H 'Origin: http://18-228-94-6.sslip.io:8081'"
Write-Host "Resposta via Caddy: $municipiosCaddyResponse" -ForegroundColor Cyan

# Testar lotes via Caddy
Write-Host "Testando /api/lotes via Caddy..." -ForegroundColor Cyan
$lotesCaddyResponse = ssh ubuntu@18-228-94-6.sslip.io "curl -s -X GET http://18-228-94-6.sslip.io/api/lotes -H 'Authorization: Bearer $token' -H 'Origin: http://18-228-94-6.sslip.io:8081'"
Write-Host "Resposta via Caddy: $lotesCaddyResponse" -ForegroundColor Cyan

# 5. Verificar logs da API
Write-Host "`n5. Verificando logs da API..." -ForegroundColor Yellow
ssh ubuntu@18-228-94-6.sslip.io "docker logs api --tail 10"

Write-Host "`n=== Teste concluído ===" -ForegroundColor Green
