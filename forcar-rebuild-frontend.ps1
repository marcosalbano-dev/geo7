# Script para forçar rebuild completo do frontend
# Garante que o build usa a configuração de produção

Write-Host "=== FORÇANDO REBUILD DO FRONTEND ===" -ForegroundColor Green
Write-Host "Data: $(Get-Date)" -ForegroundColor Cyan
Write-Host ""

$FrontendPath = "C:\Users\marco\OneDrive\Documentos\projeto-geo7\geo7-app"

if (-not (Test-Path $FrontendPath)) {
    Write-Host "❌ Diretório do frontend não encontrado: $FrontendPath" -ForegroundColor Red
    exit 1
}

Set-Location $FrontendPath

# 1. Limpar tudo
Write-Host "1. Limpando builds anteriores..." -ForegroundColor Yellow
if (Test-Path "dist") {
    Remove-Item "dist" -Recurse -Force
    Write-Host "   ✅ Diretório dist removido" -ForegroundColor Green
}

if (Test-Path ".angular") {
    Remove-Item ".angular" -Recurse -Force
    Write-Host "   ✅ Cache do Angular removido" -ForegroundColor Green
}

# 2. Verificar environment.prod.ts
Write-Host ""
Write-Host "2. Verificando environment.prod.ts..." -ForegroundColor Yellow
$envProdPath = "src\environments\environment.prod.ts"
if (Test-Path $envProdPath) {
    $envContent = Get-Content $envProdPath -Raw
    if ($envContent -match "apiUrl:\s*'/api'") {
        Write-Host "   ✅ environment.prod.ts está correto (apiUrl: '/api')" -ForegroundColor Green
    } else {
        Write-Host "   ❌ environment.prod.ts está INCORRETO!" -ForegroundColor Red
        Write-Host "   Conteúdo atual:" -ForegroundColor Yellow
        Get-Content $envProdPath | Select-String "apiUrl"
        exit 1
    }
} else {
    Write-Host "   ❌ environment.prod.ts não encontrado!" -ForegroundColor Red
    exit 1
}

# 3. Reinstalar dependências (opcional, mas garante limpeza)
Write-Host ""
Write-Host "3. Verificando dependências..." -ForegroundColor Yellow
if (-not (Test-Path "node_modules")) {
    Write-Host "   Instalando dependências..." -ForegroundColor Cyan
    npm install
} else {
    Write-Host "   ✅ node_modules encontrado" -ForegroundColor Green
}

# 4. Build de produção
Write-Host ""
Write-Host "4. Fazendo build de PRODUÇÃO..." -ForegroundColor Yellow
Write-Host "   Comando: npm run build:prod" -ForegroundColor Cyan
npm run build:prod

if ($LASTEXITCODE -ne 0) {
    Write-Host "   ❌ Erro no build!" -ForegroundColor Red
    exit 1
}

Write-Host "   ✅ Build concluído" -ForegroundColor Green

# 5. Verificar build
Write-Host ""
Write-Host "5. Verificando build gerado..." -ForegroundColor Yellow
$distPath = "dist\geo7-app\browser"
if (Test-Path $distPath) {
    Write-Host "   ✅ Build encontrado em: $distPath" -ForegroundColor Green
    
    $mainJs = Get-ChildItem $distPath -Filter "main*.js" -Recurse | Select-Object -First 1
    if ($mainJs) {
        Write-Host "   Arquivo JavaScript: $($mainJs.Name)" -ForegroundColor Cyan
        Write-Host "   Tamanho: $([math]::Round($mainJs.Length / 1KB, 2)) KB" -ForegroundColor Cyan
        
        # Verificar se contém URLs absolutas
        Write-Host ""
        Write-Host "   Verificando URLs no build..." -ForegroundColor Cyan
        $jsContent = Get-Content $mainJs.FullName -Raw -ErrorAction SilentlyContinue
        
        if ($jsContent -match "https://18-228-94-6\.sslip\.io") {
            Write-Host "   ❌ PROBLEMA: Build contém URLs absolutas HTTPS!" -ForegroundColor Red
            Write-Host "   Isso indica que o build NÃO usou environment.prod.ts" -ForegroundColor Yellow
            Write-Host ""
            Write-Host "   Verificando qual environment foi usado..." -ForegroundColor Yellow
            if ($jsContent -match "http://localhost:8080") {
                Write-Host "   ❌ Build usou environment.ts (desenvolvimento)!" -ForegroundColor Red
                Write-Host "   Isso significa que 'npm run build:prod' não funcionou corretamente" -ForegroundColor Yellow
            }
        } elseif ($jsContent -match "/api") {
            Write-Host "   ✅ Build contém '/api' (URL relativa - correto!)" -ForegroundColor Green
        } else {
            Write-Host "   ⚠️ Não foi possível verificar URLs no build" -ForegroundColor Yellow
        }
    }
    
    $indexPath = "$distPath\index.html"
    if (Test-Path $indexPath) {
        Write-Host ""
        Write-Host "   ✅ index.html encontrado" -ForegroundColor Green
    } else {
        Write-Host ""
        Write-Host "   ❌ index.html não encontrado!" -ForegroundColor Red
    }
} else {
    Write-Host "   ❌ Build não encontrado!" -ForegroundColor Red
    exit 1
}

# 6. Resumo
Write-Host ""
Write-Host "=== REBUILD CONCLUÍDO ===" -ForegroundColor Green
Write-Host ""
Write-Host "Próximos passos:" -ForegroundColor Cyan
Write-Host "1. Execute: .\atualizar-local-completo.ps1" -ForegroundColor White
Write-Host "2. Execute: .\upload-ec2.ps1" -ForegroundColor White
Write-Host "3. Na EC2, execute: ./atualizar-ec2.sh" -ForegroundColor White
Write-Host ""
Write-Host "⚠️ IMPORTANTE: Limpe o cache do navegador após atualizar!" -ForegroundColor Yellow

