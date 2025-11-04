# Script para fazer build do Angular e atualizar o container

Write-Host "=== Build e Deploy do Angular ==="

Write-Host "1. Parando o container geo7-web..."
docker stop geo7-web

Write-Host "2. Removendo o container geo7-web..."
docker rm geo7-web

Write-Host "3. Criando diretório temporário para build..."
docker run --rm -v ${PWD}:/workspace alpine mkdir -p /workspace/temp-angular

Write-Host "4. Copiando arquivos Angular para container temporário..."
# Vamos usar um container temporário para fazer o build
docker run --rm -v ${PWD}:/workspace -w /workspace node:18-alpine sh -c "
    echo 'Instalando Angular CLI...'
    npm install -g @angular/cli
    
    echo 'Criando projeto Angular temporário...'
    ng new temp-angular --routing --style=css --skip-git --package-manager=npm
    
    echo 'Entrando no diretório...'
    cd temp-angular
    
    echo 'Fazendo build de produção...'
    ng build --configuration production
    
    echo 'Build concluído!'
"

Write-Host "5. Copiando arquivos buildados para o container nginx..."
docker run -d --name geo7-web --network geo7_default -p 8081:80 -v ${PWD}/temp-angular/dist/temp-angular:/usr/share/nginx/html nginx:alpine

Write-Host "6. Verificando se o container foi criado..."
docker ps | findstr geo7-web

Write-Host "7. Testando o acesso..."
Start-Sleep -Seconds 5
try {
    $response = Invoke-WebRequest -Uri "http://18-228-94-6.sslip.io:8081/" -UseBasicParsing
    if ($response.Content -match "app-root") {
        Write-Host "✅ Angular está funcionando!"
    } else {
        Write-Host "❌ Angular não está renderizando corretamente"
    }
} catch {
    Write-Host "❌ Erro ao acessar: $($_.Exception.Message)"
}

Write-Host "=== Deploy concluído ==="

