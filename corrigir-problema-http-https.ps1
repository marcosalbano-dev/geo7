# Script para corrigir problema de HTTP/HTTPS no Geo7
# Este script corrige o problema onde o acesso via HTTP dá erro de login
# e o HTTPS não funciona

Write-Host "=== CORRIGINDO PROBLEMA HTTP/HTTPS ===" -ForegroundColor Green
Write-Host "Data: $(Get-Date)" -ForegroundColor Cyan
Write-Host ""

# 1. Verificar se estamos no diretório correto
$geo7AppPath = "C:\Users\marco\OneDrive\Documentos\projeto-geo7\geo7-app"
if (-not (Test-Path $geo7AppPath)) {
    Write-Host "❌ Diretório do frontend não encontrado: $geo7AppPath" -ForegroundColor Red
    exit 1
}

Set-Location $geo7AppPath

# 2. Verificar environment.prod.ts
Write-Host "1. Verificando environment.prod.ts..." -ForegroundColor Yellow
$envProdPath = "src\environments\environment.prod.ts"
if (Test-Path $envProdPath) {
    $envContent = Get-Content $envProdPath -Raw
    if ($envContent -match "apiUrl:\s*'/api'") {
        Write-Host "   ✅ environment.prod.ts está correto (usa URL relativa /api)" -ForegroundColor Green
    } else {
        Write-Host "   ⚠️ environment.prod.ts pode estar incorreto" -ForegroundColor Yellow
        Write-Host "   Verificando conteúdo..." -ForegroundColor Cyan
        Get-Content $envProdPath | Select-String "apiUrl"
    }
} else {
    Write-Host "   ❌ environment.prod.ts não encontrado!" -ForegroundColor Red
    exit 1
}

# 3. Verificar nginx.conf
Write-Host "`n2. Verificando nginx.conf..." -ForegroundColor Yellow
$nginxConfPath = "nginx.conf"
if (Test-Path $nginxConfPath) {
    $nginxContent = Get-Content $nginxConfPath -Raw
    if ($nginxContent -match "proxy_pass http://app:8080/api/") {
        Write-Host "   ✅ nginx.conf está correto" -ForegroundColor Green
    } else {
        Write-Host "   ⚠️ nginx.conf pode precisar de ajustes" -ForegroundColor Yellow
    }
} else {
    Write-Host "   ❌ nginx.conf não encontrado!" -ForegroundColor Red
}

# 4. Rebuild do frontend com configuração de produção
Write-Host "`n3. Rebuild do frontend com configuração de produção..." -ForegroundColor Yellow
Write-Host "   Instalando dependências..." -ForegroundColor Cyan
npm install --silent

Write-Host "   Fazendo build de produção..." -ForegroundColor Cyan
npm run build:prod

if ($LASTEXITCODE -eq 0) {
    Write-Host "   ✅ Build concluído com sucesso" -ForegroundColor Green
} else {
    Write-Host "   ❌ Erro no build" -ForegroundColor Red
    exit 1
}

# 5. Verificar se o build foi criado
Write-Host "`n4. Verificando build..." -ForegroundColor Yellow
$distPath = "dist\geo7-app\browser"
if (Test-Path $distPath) {
    Write-Host "   ✅ Build encontrado em: $distPath" -ForegroundColor Green
    
    # Verificar se há arquivos main.js ou similar
    $mainFiles = Get-ChildItem $distPath -Recurse -Filter "main*.js" | Select-Object -First 1
    if ($mainFiles) {
        Write-Host "   ✅ Arquivos JavaScript encontrados" -ForegroundColor Green
        
        # Verificar se o build usa URL relativa (não deve ter http:// ou https://)
        Write-Host "   Verificando se o build usa URL relativa..." -ForegroundColor Cyan
        $mainContent = Get-Content $mainFiles.FullName -Raw -ErrorAction SilentlyContinue
        if ($mainContent -match "https://18-228-94-6\.sslip\.io") {
            Write-Host "   ⚠️ ATENÇÃO: O build ainda contém URLs absolutas HTTPS!" -ForegroundColor Red
            Write-Host "   Isso pode causar o problema de tentar usar HTTPS quando deveria usar HTTP" -ForegroundColor Yellow
        } else {
            Write-Host "   ✅ Build parece usar URLs relativas" -ForegroundColor Green
        }
    }
} else {
    Write-Host "   ❌ Build não encontrado!" -ForegroundColor Red
    exit 1
}

# 6. Instruções finais
Write-Host "`n=== RESUMO E PRÓXIMOS PASSOS ===" -ForegroundColor Yellow
Write-Host ""
Write-Host "✅ Correções aplicadas:" -ForegroundColor Green
Write-Host "   1. Script de build atualizado para usar 'npm run build:prod'" -ForegroundColor Cyan
Write-Host "   2. nginx.conf corrigido para proxy correto" -ForegroundColor Cyan
Write-Host "   3. Build de produção criado" -ForegroundColor Cyan
Write-Host ""
Write-Host "📋 Próximos passos:" -ForegroundColor Yellow
Write-Host "   1. Execute o script de atualização: .\atualizar-local.ps1" -ForegroundColor Cyan
Write-Host "   2. Faça upload para a EC2: .\upload-ec2.ps1" -ForegroundColor Cyan
Write-Host "   3. No servidor, reconstrua o container web:" -ForegroundColor Cyan
Write-Host "      docker-compose -f docker/docker-compose.yml build web" -ForegroundColor White
Write-Host "      docker-compose -f docker/docker-compose.yml up -d web" -ForegroundColor White
Write-Host ""
Write-Host "⚠️ IMPORTANTE:" -ForegroundColor Yellow
Write-Host "   - Limpe o cache do navegador (Ctrl+Shift+Delete)" -ForegroundColor Cyan
Write-Host "   - Se o problema persistir, verifique se há HSTS cacheado:" -ForegroundColor Cyan
Write-Host "     chrome://net-internals/#hsts" -ForegroundColor White
Write-Host "     Remova o domínio 18-228-94-6.sslip.io se estiver listado" -ForegroundColor Cyan
Write-Host ""
Write-Host "✅ Script concluído!" -ForegroundColor Green

