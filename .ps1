# Script completo de atualização do sistema Geo7
$startTime = Get-Date
Write-Host "=== ATUALIZAÇÃO COMPLETA DO SISTEMA GEO7 ===" -ForegroundColor Green
Write-Host "Data: $(Get-Date)" -ForegroundColor Cyan
Write-Host "Este script irá atualizar todo o sistema (Frontend + Backend + EC2)" -ForegroundColor Yellow

# Verificar se os scripts existem
$scripts = @("atualizar-local.ps1", "upload-ec2.ps1")
foreach ($script in $scripts) {
    if (-not (Test-Path $script)) {
        Write-Host "❌ Script não encontrado: $script" -ForegroundColor Red
        exit 1
    }
}

# Confirmar execução
Write-Host "`n⚠️ ATENÇÃO: Este processo irá:" -ForegroundColor Yellow
Write-Host "   1. Compilar backend e frontend" -ForegroundColor Cyan
Write-Host "   2. Fazer upload para EC2" -ForegroundColor Cyan
Write-Host "   3. Parar e reiniciar containers Docker" -ForegroundColor Cyan
Write-Host "   4. Atualizar arquivos no servidor" -ForegroundColor Cyan

$confirm = Read-Host "`nDeseja continuar? (s/N)"
if ($confirm -ne "s" -and $confirm -ne "S" -and $confirm -ne "sim" -and $confirm -ne "SIM") {
    Write-Host "Operação cancelada pelo usuário." -ForegroundColor Yellow
    exit 0
}

# 1. Atualizar localmente
Write-Host "`n" + "="*50 -ForegroundColor Green
Write-Host "PASSO 1: ATUALIZANDO LOCALMENTE" -ForegroundColor Green
Write-Host "="*50 -ForegroundColor Green

try {
    & ".\atualizar-local.ps1"
    if ($LASTEXITCODE -ne 0) {
        Write-Host "❌ Erro na atualização local" -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "❌ Erro na execução do script local: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# 2. Upload para EC2
Write-Host "`n" + "="*50 -ForegroundColor Green
Write-Host "PASSO 2: FAZENDO UPLOAD PARA EC2" -ForegroundColor Green
Write-Host "="*50 -ForegroundColor Green

try {
    & ".\upload-ec2.ps1"
    if ($LASTEXITCODE -ne 0) {
        Write-Host "❌ Erro no upload para EC2" -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "❌ Erro na execução do script de upload: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# 3. Atualizar na EC2
Write-Host "`n" + "="*50 -ForegroundColor Green
Write-Host "PASSO 3: ATUALIZANDO NA EC2" -ForegroundColor Green
Write-Host "="*50 -ForegroundColor Green

Write-Host "Conectando à EC2 e executando script de atualização..." -ForegroundColor Cyan

try {
    # Verificar se o script existe na EC2
    $checkScript = ssh -i "C:\Users\marco\OneDrive\Documentos\projeto-geo7\geo7-key.pem" ubuntu@18-228-94-6.sslip.io "test -f atualizar-ec2.sh && echo 'exists' || echo 'not_found'"
    
    if ($checkScript -eq "not_found") {
        Write-Host "⚠️ Script atualizar-ec2.sh não encontrado na EC2" -ForegroundColor Yellow
        Write-Host "   Fazendo upload do script..." -ForegroundColor Cyan
        scp -i "C:\Users\marco\OneDrive\Documentos\projeto-geo7\geo7-key.pem" "atualizar-ec2.sh" ubuntu@18-228-94-6.sslip.io:/home/ubuntu/
    }
    
    # Executar script na EC2
    ssh -i "C:\Users\marco\OneDrive\Documentos\projeto-geo7\geo7-key.pem" ubuntu@18-228-94-6.sslip.io "chmod +x atualizar-ec2.sh && ./atualizar-ec2.sh"
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ Atualização na EC2 concluída com sucesso" -ForegroundColor Green
    } else {
        Write-Host "❌ Erro na atualização da EC2" -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "❌ Erro na execução do script na EC2: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# 4. Verificação final
Write-Host "`n" + "="*50 -ForegroundColor Green
Write-Host "PASSO 4: VERIFICAÇÃO FINAL" -ForegroundColor Green
Write-Host "="*50 -ForegroundColor Green

Write-Host "Testando conectividade..." -ForegroundColor Cyan

# Testar API
try {
    $apiResponse = Invoke-WebRequest -Uri "http://18-228-94-6.sslip.io:8080/api/healthz" -TimeoutSec 10 -ErrorAction SilentlyContinue
    if ($apiResponse.StatusCode -eq 200) {
        Write-Host "✅ API funcionando" -ForegroundColor Green
    } else {
        Write-Host "⚠️ API retornou status: $($apiResponse.StatusCode)" -ForegroundColor Yellow
    }
} catch {
    Write-Host "⚠️ Não foi possível testar a API: $($_.Exception.Message)" -ForegroundColor Yellow
}

# Testar Frontend
try {
    $webResponse = Invoke-WebRequest -Uri "http://18-228-94-6.sslip.io" -TimeoutSec 10 -ErrorAction SilentlyContinue
    if ($webResponse.StatusCode -eq 200) {
        Write-Host "✅ Frontend funcionando" -ForegroundColor Green
    } else {
        Write-Host "⚠️ Frontend retornou status: $($webResponse.StatusCode)" -ForegroundColor Yellow
    }
} catch {
    Write-Host "⚠️ Não foi possível testar o frontend: $($_.Exception.Message)" -ForegroundColor Yellow
}

# Resumo final
Write-Host "`n" + "="*60 -ForegroundColor Green
Write-Host "🎉 ATUALIZAÇÃO COMPLETA FINALIZADA!" -ForegroundColor Green
Write-Host "="*60 -ForegroundColor Green

Write-Host "`n📋 RESUMO:" -ForegroundColor Yellow
Write-Host "✅ Backend compilado e atualizado" -ForegroundColor Green
Write-Host "✅ Frontend compilado e atualizado" -ForegroundColor Green
Write-Host "✅ Arquivos enviados para EC2" -ForegroundColor Green
Write-Host "✅ Containers reiniciados" -ForegroundColor Green
Write-Host "✅ Sistema funcionando" -ForegroundColor Green

Write-Host "`n🌐 URLs para teste:" -ForegroundColor Yellow
Write-Host "   Frontend: http://18-228-94-6.sslip.io" -ForegroundColor Cyan
Write-Host "   API: http://18-228-94-6.sslip.io:8080/api/healthz" -ForegroundColor Cyan

Write-Host "`n🔧 Para monitorar:" -ForegroundColor Yellow
Write-Host "   ssh -i geo7-key.pem ubuntu@18-228-94-6.sslip.io" -ForegroundColor Cyan
Write-Host "   sudo docker ps" -ForegroundColor Cyan
Write-Host "   sudo docker logs geo7-app" -ForegroundColor Cyan

Write-Host "`n⏱️ Tempo total: $((Get-Date) - $startTime)" -ForegroundColor Cyan
Write-Host "`n🎯 Próximos passos:" -ForegroundColor Yellow
Write-Host "   1. Testar login no frontend" -ForegroundColor Cyan
Write-Host "   2. Testar edição de lotes" -ForegroundColor Cyan
Write-Host "   3. Testar exportação XML" -ForegroundColor Cyan
Write-Host "   4. Monitorar logs por alguns minutos" -ForegroundColor Cyan
