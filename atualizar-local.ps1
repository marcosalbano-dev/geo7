# Script para atualizar backend e frontend localmente
Write-Host "=== ATUALIZANDO SISTEMA LOCAL ===" -ForegroundColor Green
Write-Host "Data: $(Get-Date)" -ForegroundColor Cyan

# 1. Atualizar Backend
Write-Host "`n1. Atualizando Backend..." -ForegroundColor Yellow
Set-Location "C:\Users\marco\OneDrive\Documentos\projeto-geo7\geo7"

Write-Host "   Compilando projeto Java..." -ForegroundColor Cyan
mvn clean package -DskipTests -q

if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ Backend compilado com sucesso" -ForegroundColor Green
} else {
    Write-Host "❌ Erro na compilação do backend" -ForegroundColor Red
    Write-Host "   Verifique os erros acima e tente novamente" -ForegroundColor Yellow
    exit 1
}

# 2. Atualizar Frontend
Write-Host "`n2. Atualizando Frontend..." -ForegroundColor Yellow
Set-Location "C:\Users\marco\OneDrive\Documentos\projeto-geo7\geo7-app"

Write-Host "   Instalando dependências..." -ForegroundColor Cyan
npm install --silent

Write-Host "   Compilando aplicação Angular (produção)..." -ForegroundColor Cyan
npm run build:prod

if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ Frontend compilado com sucesso" -ForegroundColor Green
} else {
    Write-Host "❌ Erro na compilação do frontend" -ForegroundColor Red
    Write-Host "   Verifique os erros acima e tente novamente" -ForegroundColor Yellow
    exit 1
}

# 3. Preparar arquivos
Write-Host "`n3. Preparando arquivos para upload..." -ForegroundColor Yellow

# Limpar diretório temporário
$tempPath = "C:\temp\geo7-update"
if (Test-Path $tempPath) {
    Remove-Item -Path $tempPath -Recurse -Force
}
New-Item -Path $tempPath -ItemType Directory -Force | Out-Null

# Copiar JAR do backend
$backendPath = "C:\Users\marco\OneDrive\Documentos\projeto-geo7\geo7"
$jarSource = "$backendPath\target\geo7-1.0-SNAPSHOT.jar"
$jarDest = "$tempPath\geo7-1.0-SNAPSHOT.jar"
if (Test-Path $jarSource) {
    Copy-Item $jarSource $jarDest
    Write-Host "   ✅ JAR do backend copiado" -ForegroundColor Green
} else {
    Write-Host "   ❌ JAR do backend não encontrado: $jarSource" -ForegroundColor Red
    exit 1
}

# Copiar arquivos do frontend
$frontendSource = "dist\geo7-app"
$frontendDest = "$tempPath\frontend"
if (Test-Path $frontendSource) {
    Copy-Item $frontendSource $frontendDest -Recurse -Force
    Write-Host "   ✅ Arquivos do frontend copiados" -ForegroundColor Green
} else {
    Write-Host "   ❌ Arquivos do frontend não encontrados" -ForegroundColor Red
    exit 1
}

Write-Host "`n✅ ARQUIVOS PREPARADOS COM SUCESSO!" -ForegroundColor Green
Write-Host "`nLocalização: $tempPath" -ForegroundColor Cyan
Write-Host "`nPróximo passo: Execute .\upload-ec2.ps1" -ForegroundColor Yellow

# Mostrar resumo
Write-Host "`n=== RESUMO ===" -ForegroundColor Yellow
Write-Host "Backend: $jarDest" -ForegroundColor Cyan
Write-Host "Frontend: $frontendDest" -ForegroundColor Cyan
Write-Host "`nTamanho total: $((Get-ChildItem $tempPath -Recurse | Measure-Object -Property Length -Sum).Sum / 1MB) MB" -ForegroundColor Cyan
