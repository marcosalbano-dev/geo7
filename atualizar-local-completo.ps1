# Script completo para atualizar backend e frontend localmente
# Garante que o código está atualizado e compilado corretamente

Write-Host "=== ATUALIZAÇÃO COMPLETA DO SISTEMA ===" -ForegroundColor Green
Write-Host "Data: $(Get-Date)" -ForegroundColor Cyan
Write-Host ""

$ErrorActionPreference = "Stop"

# Caminhos
$BackendPath = "C:\Users\marco\OneDrive\Documentos\projeto-geo7\geo7"
$FrontendPath = "C:\Users\marco\OneDrive\Documentos\projeto-geo7\geo7-app"
$TempPath = "C:\temp\geo7-update"

# 1. Atualizar Backend
Write-Host "1. ATUALIZANDO BACKEND..." -ForegroundColor Yellow
Set-Location $BackendPath

# Verificar branch
Write-Host "   Verificando branch..." -ForegroundColor Gray
$currentBranch = git rev-parse --abbrev-ref HEAD
Write-Host "   Branch atual: $currentBranch" -ForegroundColor Gray

if ($currentBranch -ne "feat_campos_obrigatorios") {
    Write-Host "   ⚠️ Branch diferente! Mudando para feat_campos_obrigatorios..." -ForegroundColor Yellow
    git checkout feat_campos_obrigatorios
    git pull origin feat_campos_obrigatorios
}

# Compilar
Write-Host "   Compilando backend..." -ForegroundColor Gray
mvn clean package -DskipTests -q

if ($LASTEXITCODE -eq 0) {
    Write-Host "   ✅ Backend compilado com sucesso" -ForegroundColor Green
    $jarPath = "$BackendPath\target\geo7-1.0-SNAPSHOT.jar"
    if (Test-Path $jarPath) {
        $jarSize = (Get-Item $jarPath).Length / 1MB
        $jarDate = (Get-Item $jarPath).LastWriteTime
        Write-Host "   JAR: $([math]::Round($jarSize, 2)) MB - Modificado: $jarDate" -ForegroundColor Gray
    }
} else {
    Write-Host "   ❌ Erro na compilação do backend" -ForegroundColor Red
    exit 1
}

Write-Host ""

# 2. Atualizar Frontend
Write-Host "2. ATUALIZANDO FRONTEND..." -ForegroundColor Yellow
Set-Location $FrontendPath

# Verificar branch
Write-Host "   Verificando branch..." -ForegroundColor Gray
$currentBranch = git rev-parse --abbrev-ref HEAD
Write-Host "   Branch atual: $currentBranch" -ForegroundColor Gray

if ($currentBranch -ne "feat_campos_obrigatorios_front") {
    Write-Host "   ⚠️ Branch diferente! Mudando para feat_campos_obrigatorios_front..." -ForegroundColor Yellow
    git checkout feat_campos_obrigatorios_front
    git pull origin feat_campos_obrigatorios_front
}

# Limpar build anterior
Write-Host "   Limpando build anterior..." -ForegroundColor Gray
if (Test-Path "$FrontendPath\dist") {
    Remove-Item "$FrontendPath\dist" -Recurse -Force
}

# Instalar dependências (se necessário)
Write-Host "   Verificando dependências..." -ForegroundColor Gray
if (!(Test-Path "$FrontendPath\node_modules")) {
    Write-Host "   Instalando dependências..." -ForegroundColor Gray
    npm install
}

# Compilar
Write-Host "   Compilando frontend..." -ForegroundColor Gray
npm run build

if ($LASTEXITCODE -eq 0) {
    Write-Host "   ✅ Frontend compilado com sucesso" -ForegroundColor Green
    
    # Verificar estrutura
    $indexPath = "$FrontendPath\dist\geo7-app\browser\index.html"
    if (Test-Path $indexPath) {
        $indexDate = (Get-Item $indexPath).LastWriteTime
        Write-Host "   index.html encontrado em dist/geo7-app/browser/" -ForegroundColor Gray
        Write-Host "   Modificado: $indexDate" -ForegroundColor Gray
    } else {
        Write-Host "   ⚠️ AVISO: index.html não encontrado em dist/geo7-app/browser/" -ForegroundColor Yellow
        # Tentar encontrar
        $foundIndex = Get-ChildItem "$FrontendPath\dist" -Filter "index.html" -Recurse | Select-Object -First 1
        if ($foundIndex) {
            Write-Host "   Encontrado em: $($foundIndex.FullName)" -ForegroundColor Yellow
        }
    }
} else {
    Write-Host "   ❌ Erro na compilação do frontend" -ForegroundColor Red
    exit 1
}

Write-Host ""

# 3. Preparar arquivos
Write-Host "3. PREPARANDO ARQUIVOS PARA UPLOAD..." -ForegroundColor Yellow

# Limpar diretório temporário
if (Test-Path $TempPath) {
    Remove-Item $TempPath -Recurse -Force
}
New-Item -Path $TempPath -ItemType Directory -Force | Out-Null
New-Item -Path "$TempPath\frontend" -ItemType Directory -Force | Out-Null

# Copiar JAR
Write-Host "   Copiando JAR..." -ForegroundColor Gray
Copy-Item "$BackendPath\target\geo7-1.0-SNAPSHOT.jar" "$TempPath\" -Force

# Copiar frontend (do diretório browser/)
Write-Host "   Copiando frontend..." -ForegroundColor Gray
$browserPath = "$FrontendPath\dist\geo7-app\browser"
if (Test-Path $browserPath) {
    Copy-Item "$browserPath\*" "$TempPath\frontend\" -Recurse -Force
    Write-Host "   ✅ Frontend copiado de dist/geo7-app/browser/" -ForegroundColor Green
} else {
    # Fallback: tentar copiar de dist/geo7-app/
    Write-Host "   ⚠️ Diretório browser/ não encontrado, tentando dist/geo7-app/" -ForegroundColor Yellow
    Copy-Item "$FrontendPath\dist\geo7-app\*" "$TempPath\frontend\" -Recurse -Force
}

# Verificar se index.html foi copiado
if (Test-Path "$TempPath\frontend\index.html") {
    Write-Host "   ✅ index.html encontrado no diretório de upload" -ForegroundColor Green
    $indexSize = (Get-Item "$TempPath\frontend\index.html").Length / 1KB
    Write-Host "   Tamanho: $([math]::Round($indexSize, 2)) KB" -ForegroundColor Gray
} else {
    Write-Host "   ❌ ERRO: index.html não encontrado no diretório de upload!" -ForegroundColor Red
    Write-Host "   Conteúdo de $TempPath\frontend:" -ForegroundColor Yellow
    Get-ChildItem "$TempPath\frontend" | Select-Object Name, Length | Format-Table
    exit 1
}

Write-Host ""
Write-Host "✅ ARQUIVOS PREPARADOS EM: $TempPath" -ForegroundColor Green
Write-Host ""
Write-Host "Próximos passos:" -ForegroundColor Cyan
Write-Host "1. Execute: .\upload-ec2.ps1" -ForegroundColor White
Write-Host "2. Na EC2, execute: ./atualizar-ec2.sh" -ForegroundColor White
Write-Host "3. Limpe o cache do navegador (Ctrl+Shift+R)" -ForegroundColor White
Write-Host ""


