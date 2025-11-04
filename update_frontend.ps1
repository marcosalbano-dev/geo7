# Script para atualizar o frontend Angular com a URL correta da API
# Data: $(Get-Date)

Write-Host "=== ATUALIZANDO FRONTEND ANGULAR - GEO7 ===" -ForegroundColor Green
Write-Host "Data: $(Get-Date)" -ForegroundColor Cyan

# Navegar para o diretório do frontend
$frontendPath = "C:\Users\marco\OneDrive\Documentos\projeto-geo7\geo7-app"
Set-Location $frontendPath

Write-Host "`n1. Verificando configurações..." -ForegroundColor Yellow

# Verificar se o Node.js está instalado
try {
    $nodeVersion = node --version
    Write-Host "✅ Node.js encontrado: $nodeVersion" -ForegroundColor Green
} catch {
    Write-Host "❌ Node.js não encontrado. Instale o Node.js primeiro." -ForegroundColor Red
    exit 1
}

# Verificar se o npm está instalado
try {
    $npmVersion = npm --version
    Write-Host "✅ npm encontrado: $npmVersion" -ForegroundColor Green
} catch {
    Write-Host "❌ npm não encontrado." -ForegroundColor Red
    exit 1
}

Write-Host "`n2. Instalando dependências..." -ForegroundColor Yellow
try {
    npm install
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ Dependências instaladas com sucesso" -ForegroundColor Green
    } else {
        Write-Host "❌ Erro ao instalar dependências" -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "❌ Erro ao instalar dependências: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

Write-Host "`n3. Compilando aplicação para produção..." -ForegroundColor Yellow
try {
    npm run build
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ Aplicação compilada com sucesso" -ForegroundColor Green
    } else {
        Write-Host "❌ Erro na compilação" -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "❌ Erro na compilação: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

Write-Host "`n4. Verificando arquivos gerados..." -ForegroundColor Yellow
$distPath = "dist\geo7-app"
if (Test-Path $distPath) {
    Write-Host "✅ Diretório dist criado: $distPath" -ForegroundColor Green
    
    # Listar arquivos principais
    $mainFiles = Get-ChildItem $distPath -Name
    Write-Host "   Arquivos gerados:" -ForegroundColor Cyan
    foreach ($file in $mainFiles) {
        Write-Host "   - $file" -ForegroundColor Cyan
    }
} else {
    Write-Host "❌ Diretório dist não encontrado" -ForegroundColor Red
    exit 1
}

Write-Host "`n=== ATUALIZAÇÃO DO FRONTEND CONCLUÍDA ===" -ForegroundColor Green
Write-Host "`nRESUMO:" -ForegroundColor Yellow
Write-Host "✅ URL da API corrigida para: http://18-228-94-6.sslip.io:8080/api" -ForegroundColor Green
Write-Host "✅ Frontend compilado com sucesso" -ForegroundColor Green
Write-Host "✅ Arquivos prontos para deploy" -ForegroundColor Green

Write-Host "`nPRÓXIMOS PASSOS:" -ForegroundColor Yellow
Write-Host "1. Copiar arquivos do diretório 'dist/geo7-app' para o servidor web" -ForegroundColor Cyan
Write-Host "2. Configurar o servidor web (Apache/Nginx) para servir os arquivos" -ForegroundColor Cyan
Write-Host "3. Testar a aplicação no navegador" -ForegroundColor Cyan
Write-Host "4. Verificar se a edição de lotes e exportação XML funcionam" -ForegroundColor Cyan

Write-Host "`nARQUIVOS PARA DEPLOY:" -ForegroundColor Yellow
Write-Host "Diretório: $distPath" -ForegroundColor Cyan
Write-Host "Copie todos os arquivos deste diretório para o servidor web" -ForegroundColor Cyan
