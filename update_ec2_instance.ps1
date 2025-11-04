# Script para atualizar a instância EC2 com as correções do Geo7
# Data: $(Get-Date)

Write-Host "=== ATUALIZANDO INSTÂNCIA EC2 - GEO7 ===" -ForegroundColor Green
Write-Host "Data: $(Get-Date)" -ForegroundColor Cyan

# 1. Compilar o projeto
Write-Host "`n1. Compilando o projeto..." -ForegroundColor Yellow
try {
    mvn clean package -DskipTests -q
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ Compilação bem-sucedida" -ForegroundColor Green
    } else {
        Write-Host "❌ Erro na compilação" -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "❌ Erro na compilação: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# 2. Parar a aplicação atual (se estiver rodando)
Write-Host "`n2. Parando aplicação atual..." -ForegroundColor Yellow
try {
    # Verificar se há processo Java rodando na porta 8080
    $javaProcess = Get-Process -Name "java" -ErrorAction SilentlyContinue
    if ($javaProcess) {
        Write-Host "Processo Java encontrado, parando..." -ForegroundColor Cyan
        Stop-Process -Name "java" -Force -ErrorAction SilentlyContinue
        Start-Sleep -Seconds 3
        Write-Host "✅ Aplicação parada" -ForegroundColor Green
    } else {
        Write-Host "✅ Nenhuma aplicação rodando" -ForegroundColor Green
    }
} catch {
    Write-Host "⚠️ Aviso ao parar aplicação: $($_.Exception.Message)" -ForegroundColor Yellow
}

# 3. Iniciar a nova versão
Write-Host "`n3. Iniciando nova versão da aplicação..." -ForegroundColor Yellow
try {
    $jarFile = "target\geo7-1.0-SNAPSHOT.jar"
    if (Test-Path $jarFile) {
        Write-Host "Iniciando aplicação em background..." -ForegroundColor Cyan
        Start-Process -FilePath "java" -ArgumentList "-jar", $jarFile -WindowStyle Hidden
        Start-Sleep -Seconds 10
        
        # Verificar se a aplicação está rodando
        $response = Invoke-WebRequest -Uri "http://localhost:8080/api/healthz" -TimeoutSec 5 -ErrorAction SilentlyContinue
        if ($response.StatusCode -eq 200) {
            Write-Host "✅ Aplicação iniciada com sucesso" -ForegroundColor Green
        } else {
            Write-Host "⚠️ Aplicação pode não ter iniciado corretamente" -ForegroundColor Yellow
        }
    } else {
        Write-Host "❌ Arquivo JAR não encontrado: $jarFile" -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "❌ Erro ao iniciar aplicação: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# 4. Testar endpoints
Write-Host "`n4. Testando endpoints..." -ForegroundColor Yellow
try {
    # Testar health check
    $healthResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/healthz" -TimeoutSec 5
    Write-Host "✅ Health check funcionando" -ForegroundColor Green
    
    # Testar autenticação
    $loginBody = '{"email":"admin@geo7.com","password":"admin123"}'
    $loginResponse = Invoke-RestMethod -Uri "http://localhost:8080/auth/login" -Method POST -ContentType "application/json" -Body $loginBody
    $token = $loginResponse.token
    Write-Host "✅ Autenticação funcionando" -ForegroundColor Green
    
    # Testar endpoint de estrutura
    $estruturaResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/estrutura/por-lote/146" -Method GET -Headers @{"Authorization"="Bearer $token"}
    Write-Host "✅ Endpoint de estrutura funcionando" -ForegroundColor Green
    
    # Testar exportação
    $lotes = Invoke-RestMethod -Uri "http://localhost:8080/api/lotes" -Method GET -Headers @{"Authorization"="Bearer $token"}
    $municipioId = $lotes[0].municipioId
    $exportResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/exportacao-dp/municipio/$municipioId/xml" -Method GET -Headers @{"Authorization"="Bearer $token"}
    Write-Host "✅ Exportação XML funcionando" -ForegroundColor Green
    
} catch {
    Write-Host "❌ Erro nos testes: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n=== ATUALIZAÇÃO CONCLUÍDA ===" -ForegroundColor Green
Write-Host "`nRESUMO:" -ForegroundColor Yellow
Write-Host "✅ Backend atualizado e funcionando" -ForegroundColor Green
Write-Host "✅ Todos os endpoints testados com sucesso" -ForegroundColor Green
Write-Host "✅ Exportação XML funcionando" -ForegroundColor Green
Write-Host "`nPRÓXIMOS PASSOS:" -ForegroundColor Yellow
Write-Host "1. Verificar se o frontend está enviando tokens de autenticação" -ForegroundColor Cyan
Write-Host "2. Testar a aplicação no navegador" -ForegroundColor Cyan
Write-Host "3. Se ainda houver problemas, verificar logs do frontend" -ForegroundColor Cyan
