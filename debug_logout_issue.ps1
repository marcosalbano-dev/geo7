# Script para debugar o problema após logout
Write-Host "=== Investigando problema após logout ===" -ForegroundColor Green

# 1. Testar login novamente
Write-Host "`n1. Testando login após logout..." -ForegroundColor Yellow
$loginResponse = ssh ubuntu@18-228-94-6.sslip.io "curl -s -X POST http://18-228-94-6.sslip.io:8080/auth/login -H 'Content-Type: application/json' -d '{\"email\":\"admin@geo7.com\",\"password\":\"admin123\"}'"
$loginData = $loginResponse | ConvertFrom-Json
$token = $loginData.token

if ($token) {
    Write-Host "✓ Login funcionando após logout" -ForegroundColor Green
    Write-Host "Token: $($token.Substring(0, 20))..." -ForegroundColor Cyan
} else {
    Write-Host "✗ Erro no login após logout" -ForegroundColor Red
    Write-Host "Resposta: $loginResponse" -ForegroundColor Red
}

# 2. Testar endpoints com novo token
Write-Host "`n2. Testando endpoints com novo token..." -ForegroundColor Yellow

Write-Host "Testando /municipios..." -ForegroundColor Cyan
$municipiosResponse = ssh ubuntu@18-228-94-6.sslip.io "curl -s -X GET http://18-228-94-6.sslip.io/municipios -H 'Authorization: Bearer $token' -H 'Origin: http://18-228-94-6.sslip.io:8081'"
Write-Host "Resposta: $($municipiosResponse.Substring(0, 100))..." -ForegroundColor Cyan

Write-Host "Testando /lotes..." -ForegroundColor Cyan
$lotesResponse = ssh ubuntu@18-228-94-6.sslip.io "curl -s -X GET http://18-228-94-6.sslip.io/lotes -H 'Authorization: Bearer $token' -H 'Origin: http://18-228-94-6.sslip.io:8081'"
Write-Host "Resposta: $($lotesResponse.Substring(0, 100))..." -ForegroundColor Cyan

# 3. Verificar logs da API
Write-Host "`n3. Verificando logs da API..." -ForegroundColor Yellow
ssh ubuntu@18-228-94-6.sslip.io "docker logs api --tail 10"

# 4. Verificar se há endpoint de logout
Write-Host "`n4. Verificando endpoint de logout..." -ForegroundColor Yellow
ssh ubuntu@18-228-94-6.sslip.io "curl -s -X POST http://18-228-94-6.sslip.io:8080/auth/logout -H 'Authorization: Bearer $token' -v"

Write-Host "`n=== Investigação concluída ===" -ForegroundColor Green
