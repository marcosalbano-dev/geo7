# Script para fazer upload do script atualizar-ec2.sh para a EC2
Write-Host "=== FAZENDO UPLOAD DO SCRIPT atualizar-ec2.sh ===" -ForegroundColor Green
Write-Host "Data: $(Get-Date)" -ForegroundColor Cyan

# Configurações
$keyPath = "C:\Users\marco\OneDrive\Documentos\projeto-geo7\geo7-key.pem"
$ec2Host = "ubuntu@18-228-94-6.sslip.io"
$scriptPath = "C:\Users\marco\OneDrive\Documentos\projeto-geo7\geo7\atualizar-ec2.sh"

# Função para encontrar scp.exe
function Find-Scp {
    $scp = Get-Command "scp" -ErrorAction SilentlyContinue
    if ($scp) {
        return $scp.Path
    }
    
    $possiblePaths = @(
        "C:\Windows\System32\OpenSSH\scp.exe",
        "C:\Program Files\OpenSSH\scp.exe",
        "C:\Program Files (x86)\OpenSSH\scp.exe",
        "C:\OpenSSH-Win32\scp.exe"
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
    exit 1
}

Write-Host "Usando SCP: $scpPath" -ForegroundColor Gray

# Verificar se o script existe
if (-not (Test-Path $scriptPath)) {
    Write-Host "❌ Script não encontrado: $scriptPath" -ForegroundColor Red
    exit 1
}

# Fazer upload
Write-Host "`nEnviando atualizar-ec2.sh..." -ForegroundColor Yellow
& $scpPath -i $keyPath $scriptPath "${ec2Host}:/home/ubuntu/"

if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ Script enviado com sucesso" -ForegroundColor Green
    
    # Tornar executável
    Write-Host "`nTornando script executável..." -ForegroundColor Yellow
    ssh -i $keyPath $ec2Host "chmod +x atualizar-ec2.sh"
    
    Write-Host "✅ Script está pronto para uso na EC2" -ForegroundColor Green
    Write-Host "`nNa EC2, execute: ./atualizar-ec2.sh" -ForegroundColor Cyan
} else {
    Write-Host "❌ Erro no upload do script" -ForegroundColor Red
    exit 1
}

