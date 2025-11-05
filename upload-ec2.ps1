# Script para fazer upload para EC2
Write-Host "=== FAZENDO UPLOAD PARA EC2 ===" -ForegroundColor Green
Write-Host "Data: $(Get-Date)" -ForegroundColor Cyan

# Configurações
$keyPath = "C:\Users\marco\OneDrive\Documentos\projeto-geo7\geo7-key.pem"
$ec2Host = "ubuntu@18-228-94-6.sslip.io"
$localPath = "C:\temp\geo7-update"

# Função para encontrar scp.exe
function Find-Scp {
    # Tentar encontrar scp no PATH
    $scp = Get-Command "scp" -ErrorAction SilentlyContinue
    if ($scp) {
        return $scp.Path
    }
    
    # Tentar caminhos comuns do OpenSSH no Windows
    $possiblePaths = @(
        "C:\Windows\System32\OpenSSH\scp.exe",
        "C:\Program Files\OpenSSH\scp.exe",
        "C:\Program Files (x86)\OpenSSH\scp.exe"
    )
    
    foreach ($path in $possiblePaths) {
        if (Test-Path $path) {
            return $path
        }
    }
    
    return $null
}

# Encontrar scp
$scpPath = Find-Scp
if (-not $scpPath) {
    Write-Host "❌ SCP não encontrado no sistema" -ForegroundColor Red
    Write-Host ""
    Write-Host "Para instalar o OpenSSH no Windows:" -ForegroundColor Yellow
    Write-Host "1. Abra o PowerShell como Administrador" -ForegroundColor Cyan
    Write-Host "2. Execute: Add-WindowsCapability -Online -Name OpenSSH.Client~~~~0.0.1.0" -ForegroundColor White
    Write-Host ""
    Write-Host "OU instale manualmente:" -ForegroundColor Yellow
    Write-Host "- Baixe o OpenSSH de: https://github.com/PowerShell/Win32-OpenSSH/releases" -ForegroundColor Cyan
    Write-Host "- Adicione o diretório ao PATH do sistema" -ForegroundColor Cyan
    Write-Host ""
    exit 1
}

Write-Host "Usando SCP: $scpPath" -ForegroundColor Gray

# Verificar se os arquivos existem
if (-not (Test-Path $localPath)) {
    Write-Host "❌ Diretório de arquivos não encontrado: $localPath" -ForegroundColor Red
    Write-Host "   Execute primeiro: .\atualizar-local.ps1" -ForegroundColor Yellow
    exit 1
}

if (-not (Test-Path $keyPath)) {
    Write-Host "❌ Chave SSH não encontrada: $keyPath" -ForegroundColor Red
    Write-Host "   Verifique se o arquivo geo7-key.pem existe" -ForegroundColor Yellow
    exit 1
}

# Verificar conectividade
Write-Host "`n1. Verificando conectividade com EC2..." -ForegroundColor Yellow
try {
    $pingResult = Test-NetConnection -ComputerName "18-228-94-6.sslip.io" -Port 22 -InformationLevel Quiet
    if ($pingResult) {
        Write-Host "✅ Conectividade OK" -ForegroundColor Green
    } else {
        Write-Host "❌ Não foi possível conectar à EC2" -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "❌ Erro ao verificar conectividade: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# 1. Upload do backend
Write-Host "`n2. Enviando backend..." -ForegroundColor Yellow
$jarFile = "$localPath\geo7-1.0-SNAPSHOT.jar"
if (Test-Path $jarFile) {
    Write-Host "   Enviando JAR ($([math]::Round((Get-Item $jarFile).Length / 1MB, 2)) MB)..." -ForegroundColor Cyan
    & $scpPath -i $keyPath $jarFile "${ec2Host}:/home/ubuntu/"
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ Backend enviado com sucesso" -ForegroundColor Green
    } else {
        Write-Host "❌ Erro no upload do backend" -ForegroundColor Red
        Write-Host "   Verifique a conectividade e as credenciais" -ForegroundColor Yellow
        exit 1
    }
} else {
    Write-Host "❌ Arquivo JAR não encontrado: $jarFile" -ForegroundColor Red
    exit 1
}

# 2. Upload do frontend
Write-Host "`n3. Enviando frontend..." -ForegroundColor Yellow
$frontendDir = "$localPath\frontend"
if (Test-Path $frontendDir) {
    $frontendSize = [math]::Round((Get-ChildItem $frontendDir -Recurse | Measure-Object -Property Length -Sum).Sum / 1MB, 2)
    Write-Host "   Enviando frontend ($frontendSize MB)..." -ForegroundColor Cyan
    & $scpPath -i $keyPath -r $frontendDir "${ec2Host}:/home/ubuntu/"
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ Frontend enviado com sucesso" -ForegroundColor Green
    } else {
        Write-Host "❌ Erro no upload do frontend" -ForegroundColor Red
        Write-Host "   Verifique a conectividade e as credenciais" -ForegroundColor Yellow
        exit 1
    }
} else {
    Write-Host "❌ Diretório do frontend não encontrado: $frontendDir" -ForegroundColor Red
    exit 1
}

Write-Host "`n✅ UPLOAD CONCLUÍDO COM SUCESSO!" -ForegroundColor Green
Write-Host "`nPróximo passo: Execute o script de atualização na EC2" -ForegroundColor Yellow
Write-Host "`nComando SSH:" -ForegroundColor Cyan
Write-Host "ssh -i geo7-key.pem ubuntu@18-228-94-6.sslip.io" -ForegroundColor White
Write-Host "`nDepois execute:" -ForegroundColor Cyan
Write-Host "./atualizar-ec2.sh" -ForegroundColor White
