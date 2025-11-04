# Script simples para compilar o backend
Write-Host "=== COMPILANDO BACKEND ===" -ForegroundColor Green

# Parar qualquer processo Java que possa estar rodando
Write-Host "Parando processos Java..." -ForegroundColor Yellow
Get-Process | Where-Object {$_.ProcessName -like "*java*"} | Stop-Process -Force -ErrorAction SilentlyContinue

# Aguardar um pouco
Start-Sleep -Seconds 2

# Remover pasta target se existir
Write-Host "Removendo pasta target..." -ForegroundColor Yellow
if (Test-Path "target") {
    Remove-Item -Path "target" -Recurse -Force -ErrorAction SilentlyContinue
}

# Compilar com Maven
Write-Host "Compilando com Maven..." -ForegroundColor Yellow
mvn clean package -DskipTests

if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ Backend compilado com sucesso!" -ForegroundColor Green
    
    # Verificar se o JAR foi criado
    if (Test-Path "target\geo7-1.0-SNAPSHOT.jar") {
        $jarSize = (Get-Item "target\geo7-1.0-SNAPSHOT.jar").Length / 1MB
        Write-Host "✅ JAR criado: target\geo7-1.0-SNAPSHOT.jar ($jarSize MB)" -ForegroundColor Green
    } else {
        Write-Host "❌ JAR não encontrado" -ForegroundColor Red
    }
} else {
    Write-Host "❌ Erro na compilação" -ForegroundColor Red
    Write-Host "Tentando compilar sem clean..." -ForegroundColor Yellow
    mvn package -DskipTests
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ Compilação bem-sucedida (sem clean)" -ForegroundColor Green
    } else {
        Write-Host "❌ Falha na compilação" -ForegroundColor Red
    }
}
