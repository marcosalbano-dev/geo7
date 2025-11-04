# Script para diagnosticar e corrigir problemas de autenticação no frontend
# e exportação XML

Write-Host "=== DIAGNÓSTICO DE PROBLEMAS GEO7 ===" -ForegroundColor Green

# 1. Testar autenticação
Write-Host "`n1. Testando autenticação..." -ForegroundColor Yellow
try {
    $body = '{"email":"admin@geo7.com","password":"admin123"}'
    $loginResponse = Invoke-RestMethod -Uri "http://18-228-94-6.sslip.io:8080/auth/login" -Method POST -ContentType "application/json" -Body $body
    $token = $loginResponse.token
    Write-Host "✅ Autenticação funcionando. Token obtido." -ForegroundColor Green
} catch {
    Write-Host "❌ Erro na autenticação: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# 2. Testar endpoints com autenticação
Write-Host "`n2. Testando endpoints com autenticação..." -ForegroundColor Yellow

# Testar estrutura
try {
    $estruturaResponse = Invoke-RestMethod -Uri "http://18-228-94-6.sslip.io:8080/api/estrutura/por-lote/146" -Method GET -Headers @{"Authorization"="Bearer $token"}
    Write-Host "✅ Endpoint de estrutura funcionando" -ForegroundColor Green
    Write-Host "   Dados encontrados: $($estruturaResponse -ne $null)" -ForegroundColor Cyan
} catch {
    Write-Host "❌ Erro no endpoint de estrutura: $($_.Exception.Message)" -ForegroundColor Red
}

# Testar lote
try {
    $loteResponse = Invoke-RestMethod -Uri "http://18-228-94-6.sslip.io:8080/api/lotes/146" -Method GET -Headers @{"Authorization"="Bearer $token"}
    Write-Host "✅ Endpoint de lote funcionando" -ForegroundColor Green
    Write-Host "   Dados encontrados: $($loteResponse -ne $null)" -ForegroundColor Cyan
} catch {
    Write-Host "❌ Erro no endpoint de lote: $($_.Exception.Message)" -ForegroundColor Red
}

# 3. Testar exportação XML
Write-Host "`n3. Testando exportação XML..." -ForegroundColor Yellow
try {
    $xmlResponse = Invoke-RestMethod -Uri "http://18-228-94-6.sslip.io:8080/api/exportacao-dp/municipio/1/xml" -Method GET -Headers @{"Authorization"="Bearer $token"}
    Write-Host "✅ Exportação XML funcionando" -ForegroundColor Green
    Write-Host "   Tamanho do XML: $($xmlResponse.Length) caracteres" -ForegroundColor Cyan
} catch {
    Write-Host "❌ Erro na exportação XML: $($_.Exception.Message)" -ForegroundColor Red
    
    # Tentar obter mais detalhes do erro
    try {
        $errorResponse = Invoke-WebRequest -Uri "http://18-228-94-6.sslip.io:8080/api/exportacao-dp/municipio/1/xml" -Method GET -Headers @{"Authorization"="Bearer $token"} -ErrorAction SilentlyContinue
        Write-Host "   Status Code: $($errorResponse.StatusCode)" -ForegroundColor Yellow
        Write-Host "   Response: $($errorResponse.Content)" -ForegroundColor Yellow
    } catch {
        Write-Host "   Erro detalhado: $($_.Exception.Response.StatusCode)" -ForegroundColor Yellow
    }
}

# 4. Testar endpoint JSON da exportação
Write-Host "`n4. Testando endpoint JSON da exportação..." -ForegroundColor Yellow
try {
    $jsonResponse = Invoke-RestMethod -Uri "http://18-228-94-6.sslip.io:8080/api/exportacao-dp/municipio/1" -Method GET -Headers @{"Authorization"="Bearer $token"}
    Write-Host "✅ Endpoint JSON da exportação funcionando" -ForegroundColor Green
    Write-Host "   Lotes encontrados: $($jsonResponse.lotes.Count)" -ForegroundColor Cyan
} catch {
    Write-Host "❌ Erro no endpoint JSON da exportação: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n=== DIAGNÓSTICO CONCLUÍDO ===" -ForegroundColor Green
Write-Host "`nPROBLEMAS IDENTIFICADOS:" -ForegroundColor Red
Write-Host "1. Frontend não está enviando token de autenticação nas requisições" -ForegroundColor Yellow
Write-Host "2. Possível erro no endpoint de exportação XML" -ForegroundColor Yellow
Write-Host "`nSOLUÇÕES RECOMENDADAS:" -ForegroundColor Green
Write-Host "1. Verificar se o frontend está armazenando e enviando o token JWT" -ForegroundColor Cyan
Write-Host "2. Verificar logs da aplicação para identificar erro 500 no XML" -ForegroundColor Cyan
Write-Host "3. Testar se o problema é específico do endpoint XML ou geral" -ForegroundColor Cyan
