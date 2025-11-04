# Script para atualizar o container geo7-web com os arquivos mais recentes

Write-Host "Parando o container geo7-web..."
docker stop geo7-web

Write-Host "Removendo o container geo7-web..."
docker rm geo7-web

Write-Host "Criando novo container geo7-web com arquivos mais recentes..."
docker run -d --name geo7-web --network geo7_default -p 8081:80 -v /home/ubuntu/geo7-app/dist/geo7-app:/usr/share/nginx/html nginx:alpine

Write-Host "Verificando se o container foi criado..."
docker ps | findstr geo7-web

Write-Host "Testando o acesso..."
Start-Sleep -Seconds 5
curl -s "http://18-228-94-6.sslip.io:8081/" | Select-String "app-root"

Write-Host "Container atualizado com sucesso!"

