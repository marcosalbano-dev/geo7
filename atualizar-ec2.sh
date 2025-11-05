#!/bin/bash

# Script para atualizar sistema na EC2
echo "=== ATUALIZANDO SISTEMA NA EC2 ==="
echo "Data: $(date)"

# Verificar se os arquivos existem
if [ ! -f "geo7-1.0-SNAPSHOT.jar" ]; then
    echo "❌ Arquivo JAR não encontrado"
    exit 1
fi

if [ ! -d "frontend" ]; then
    echo "❌ Diretório frontend não encontrado"
    exit 1
fi

# 1. Parar containers
echo ""
echo "1. Parando containers..."
sudo docker-compose down

if [ $? -eq 0 ]; then
    echo "✅ Containers parados com sucesso"
else
    echo "⚠️ Aviso: Erro ao parar containers (pode não estar rodando)"
fi

# 2. Fazer backup dos arquivos atuais
echo ""
echo "2. Fazendo backup dos arquivos atuais..."
sudo mkdir -p /opt/backup/$(date +%Y%m%d_%H%M%S)
sudo cp /opt/geo7/geo7-1.0-SNAPSHOT.jar /opt/backup/$(date +%Y%m%d_%H%M%S)/ 2>/dev/null || echo "   Nenhum JAR anterior encontrado"
sudo cp -r /var/www/html /opt/backup/$(date +%Y%m%d_%H%M%S)/frontend_backup 2>/dev/null || echo "   Nenhum frontend anterior encontrado"
echo "✅ Backup criado"

# 3. Atualizar backend
echo ""
echo "3. Atualizando backend..."
sudo mkdir -p /opt/geo7
sudo cp geo7-1.0-SNAPSHOT.jar /opt/geo7/
sudo chown root:root /opt/geo7/geo7-1.0-SNAPSHOT.jar
sudo chmod 644 /opt/geo7/geo7-1.0-SNAPSHOT.jar
echo "✅ Backend atualizado"

# 4. Atualizar frontend
echo ""
echo "4. Atualizando frontend..."
echo "   Removendo arquivos antigos..."
sudo rm -rf /var/www/html/*
echo "   Verificando estrutura do frontend..."

# Detectar onde está o index.html (pode estar em frontend/ ou frontend/browser/)
if [ -f "frontend/browser/index.html" ]; then
    echo "   ✅ Estrutura Angular detectada (browser/ subdiretório)"
    echo "   Copiando conteúdo de frontend/browser/ para /var/www/html/..."
    sudo cp -r frontend/browser/* /var/www/html/
elif [ -f "frontend/index.html" ]; then
    echo "   ✅ Estrutura padrão detectada"
    echo "   Copiando conteúdo de frontend/ para /var/www/html/..."
    sudo cp -r frontend/* /var/www/html/
else
    echo "   ⚠️ AVISO: index.html não encontrado em frontend/ nem frontend/browser/"
    echo "   Tentando copiar tudo mesmo assim..."
    sudo cp -r frontend/* /var/www/html/
fi

sudo chown -R www-data:www-data /var/www/html
sudo chmod -R 755 /var/www/html
echo "   Verificando arquivos atualizados..."
if [ -f "/var/www/html/index.html" ]; then
    echo "   ✅ index.html encontrado"
    echo "   Primeiras linhas do index.html:"
    head -3 /var/www/html/index.html
else
    echo "   ⚠️ AVISO: index.html não encontrado!"
    echo "   Conteúdo de /var/www/html:"
    ls -la /var/www/html/ | head -10
fi
echo "✅ Frontend atualizado"

# 5. Reiniciar containers
echo ""
echo "5. Reiniciando containers..."
sudo docker-compose up -d

if [ $? -eq 0 ]; then
    echo "✅ Containers iniciados com sucesso"
else
    echo "❌ Erro ao iniciar containers"
    exit 1
fi

# 6. Aguardar inicialização
echo ""
echo "6. Aguardando inicialização..."
sleep 15

# 6.5. Forçar reload do nginx (limpar cache interno)
echo ""
echo "6.5. Forçando reload do nginx..."
sudo docker exec geo7-web nginx -s reload 2>/dev/null || echo "   (Nginx já atualizado)"

# 7. Verificar status dos containers
echo ""
echo "7. Verificando status dos containers..."
sudo docker ps

# 8. Verificar health check
echo ""
echo "8. Verificando health check..."
for i in {1..5}; do
    if curl -s http://localhost:8080/api/healthz > /dev/null; then
        echo "✅ Backend funcionando"
        break
    else
        echo "   Tentativa $i/5 - Aguardando backend..."
        sleep 5
    fi
done

# 9. Verificar frontend
echo ""
echo "9. Verificando frontend..."
if curl -s http://localhost/ > /dev/null; then
    echo "✅ Frontend funcionando"
else
    echo "⚠️ Aviso: Frontend pode não estar funcionando"
fi

# 10. Limpeza
echo ""
echo "10. Limpando arquivos temporários..."
rm -f geo7-1.0-SNAPSHOT.jar
rm -rf frontend
echo "✅ Limpeza concluída"

# Resumo final
echo ""
echo "=== ATUALIZAÇÃO CONCLUÍDA ==="
echo ""
echo "✅ Sistema atualizado com sucesso!"
echo ""
echo "URLs para teste:"
echo "  Frontend: http://18-228-94-6.sslip.io"
echo "  API: http://18-228-94-6.sslip.io:8080/api/healthz"
echo ""
echo "Para verificar logs:"
echo "  sudo docker logs geo7-app"
echo "  sudo docker logs geo7-web"
echo ""
echo "Para monitorar:"
echo "  sudo docker ps"
echo "  curl http://localhost:8080/api/healthz"
echo ""
echo "⚠️ IMPORTANTE - Limpar cache do navegador:"
echo "  - Chrome/Edge: Ctrl+Shift+R (Windows) ou Cmd+Shift+R (Mac)"
echo "  - Firefox: Ctrl+F5 (Windows) ou Cmd+Shift+R (Mac)"
echo "  - Ou abrir em modo anônimo/privado"
