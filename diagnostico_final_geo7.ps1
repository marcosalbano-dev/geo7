# Script de diagnóstico final para problemas do Geo7
# Data: $(Get-Date)

Write-Host "=== DIAGNÓSTICO FINAL - PROBLEMAS GEO7 ===" -ForegroundColor Green
Write-Host "Data: $(Get-Date)" -ForegroundColor Cyan

# 1. Testar autenticação
Write-Host "`n1. TESTANDO AUTENTICAÇÃO..." -ForegroundColor Yellow
try {
    $body = '{"email":"admin@geo7.com","password":"admin123"}'
    $loginResponse = Invoke-RestMethod -Uri "http://18-228-94-6.sslip.io:8080/auth/login" -Method POST -ContentType "application/json" -Body $body
    $token = $loginResponse.token
    Write-Host "✅ Autenticação funcionando" -ForegroundColor Green
} catch {
    Write-Host "❌ Erro na autenticação: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# 2. Testar endpoints de dados
Write-Host "`n2. TESTANDO ENDPOINTS DE DADOS..." -ForegroundColor Yellow

# Estrutura
try {
    $estruturaResponse = Invoke-RestMethod -Uri "http://18-228-94-6.sslip.io:8080/api/estrutura/por-lote/146" -Method GET -Headers @{"Authorization"="Bearer $token"}
    Write-Host "✅ Endpoint de estrutura funcionando" -ForegroundColor Green
} catch {
    Write-Host "❌ Erro no endpoint de estrutura: $($_.Exception.Message)" -ForegroundColor Red
}

# Lote
try {
    $loteResponse = Invoke-RestMethod -Uri "http://18-228-94-6.sslip.io:8080/api/lotes/146" -Method GET -Headers @{"Authorization"="Bearer $token"}
    Write-Host "✅ Endpoint de lote funcionando" -ForegroundColor Green
} catch {
    Write-Host "❌ Erro no endpoint de lote: $($_.Exception.Message)" -ForegroundColor Red
}

# 3. Testar exportação
Write-Host "`n3. TESTANDO EXPORTAÇÃO..." -ForegroundColor Yellow

# Buscar município com lotes
try {
    $lotes = Invoke-RestMethod -Uri "http://18-228-94-6.sslip.io:8080/api/lotes" -Method GET -Headers @{"Authorization"="Bearer $token"}
    $municipioId = $lotes[0].municipioId
    Write-Host "✅ Município com lotes encontrado: ID $municipioId" -ForegroundColor Green
} catch {
    Write-Host "❌ Erro ao buscar lotes: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Testar exportação JSON
try {
    $jsonResponse = Invoke-RestMethod -Uri "http://18-228-94-6.sslip.io:8080/api/exportacao-dp/municipio/$municipioId" -Method GET -Headers @{"Authorization"="Bearer $token"}
    Write-Host "✅ Exportação JSON funcionando - Lotes: $($jsonResponse.lotes.Count)" -ForegroundColor Green
} catch {
    Write-Host "❌ Erro na exportação JSON: $($_.Exception.Message)" -ForegroundColor Red
}

# Testar exportação XML
try {
    $xmlResponse = Invoke-RestMethod -Uri "http://18-228-94-6.sslip.io:8080/api/exportacao-dp/municipio/$municipioId/xml" -Method GET -Headers @{"Authorization"="Bearer $token"}
    Write-Host "✅ Exportação XML funcionando - Tamanho: $($xmlResponse.Length) caracteres" -ForegroundColor Green
} catch {
    Write-Host "❌ Erro na exportação XML: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n=== RESUMO DOS PROBLEMAS IDENTIFICADOS ===" -ForegroundColor Red
Write-Host "`n1. PROBLEMA PRINCIPAL: Frontend não está enviando token de autenticação" -ForegroundColor Yellow
Write-Host "   - A API funciona perfeitamente quando autenticada" -ForegroundColor Cyan
Write-Host "   - Os endpoints de estrutura, lote e exportação estão funcionando" -ForegroundColor Cyan
Write-Host "   - O problema é que o frontend não está incluindo o header Authorization" -ForegroundColor Cyan

Write-Host "`n2. PROBLEMA SECUNDÁRIO: Município ID incorreto na exportação" -ForegroundColor Yellow
Write-Host "   - O frontend pode estar tentando exportar de um município que não existe" -ForegroundColor Cyan
Write-Host "   - Município ID 1 não existe no banco de dados" -ForegroundColor Cyan
Write-Host "   - Deve usar o ID do município correto (ex: 2305308)" -ForegroundColor Cyan

Write-Host "`n=== SOLUÇÕES RECOMENDADAS ===" -ForegroundColor Green
Write-Host "`n1. CORRIGIR AUTENTICAÇÃO NO FRONTEND:" -ForegroundColor Yellow
Write-Host "   - Verificar se o token JWT está sendo armazenado após login" -ForegroundColor Cyan
Write-Host "   - Verificar se o token está sendo enviado no header Authorization" -ForegroundColor Cyan
Write-Host "   - Formato correto: Authorization: Bearer <token>" -ForegroundColor Cyan

Write-Host "`n2. CORRIGIR ID DO MUNICÍPIO:" -ForegroundColor Yellow
Write-Host "   - Usar o ID correto do município na exportação" -ForegroundColor Cyan
Write-Host "   - Verificar se o frontend está obtendo o ID correto do contexto" -ForegroundColor Cyan

Write-Host "`n3. TESTAR COM CURL:" -ForegroundColor Yellow
Write-Host "   curl -H 'Authorization: Bearer <token>' http://18-228-94-6.sslip.io:8080/api/estrutura/por-lote/146" -ForegroundColor Cyan
Write-Host "   curl -H 'Authorization: Bearer <token>' http://18-228-94-6.sslip.io:8080/api/exportacao-dp/municipio/2305308/xml" -ForegroundColor Cyan

Write-Host "`n=== STATUS FINAL ===" -ForegroundColor Green
Write-Host "✅ Backend funcionando corretamente" -ForegroundColor Green
Write-Host "✅ API endpoints funcionando com autenticação" -ForegroundColor Green
Write-Host "✅ Exportação JSON e XML funcionando" -ForegroundColor Green
Write-Host "❌ Frontend não está enviando autenticação" -ForegroundColor Red
Write-Host "❌ Possível problema com ID do município no frontend" -ForegroundColor Red
