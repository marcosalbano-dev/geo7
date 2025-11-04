# Script para corrigir o container web

Write-Host "=== Corrigindo Container Web ==="

Write-Host "1. Parando todos os containers web..."
docker stop geo7-web geo7-web-test geo7-web-new 2>$null

Write-Host "2. Removendo containers antigos..."
docker rm geo7-web geo7-web-test geo7-web-new 2>$null

Write-Host "3. Criando novo container nginx..."
docker run -d --name geo7-web --network geo7_default -p 8081:80 nginx:alpine

Write-Host "4. Aguardando container inicializar..."
Start-Sleep -Seconds 5

Write-Host "5. Verificando se container está rodando..."
docker ps | findstr geo7-web

Write-Host "6. Copiando página de teste..."
docker cp test-index.html geo7-web:/usr/share/nginx/html/index.html

Write-Host "7. Testando acesso..."
try {
    $response = Invoke-WebRequest -Uri "http://18-228-94-6.sslip.io:8081/" -UseBasicParsing -TimeoutSec 10
    Write-Host "✅ Container funcionando! Status: $($response.StatusCode)"
} catch {
    Write-Host "❌ Erro: $($_.Exception.Message)"
}

Write-Host "8. Verificando porta 8081..."
netstat -an | findstr :8081

Write-Host "=== Script concluído ==="

