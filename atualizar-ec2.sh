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
BACKUP_DIR="/opt/backup/$(date +%Y%m%d_%H%M%S)"
sudo mkdir -p "$BACKUP_DIR"

# Backup do backend (verificar qual diretório está sendo usado)
if [ -f "/home/ubuntu/geo7-data/geo7-1.0-SNAPSHOT.jar" ]; then
    echo "   Fazendo backup de /home/ubuntu/geo7-data/geo7-1.0-SNAPSHOT.jar..."
    sudo cp /home/ubuntu/geo7-data/geo7-1.0-SNAPSHOT.jar "$BACKUP_DIR/" 2>/dev/null || echo "   Nenhum JAR anterior encontrado"
elif [ -f "/opt/geo7/geo7-1.0-SNAPSHOT.jar" ]; then
    echo "   Fazendo backup de /opt/geo7/geo7-1.0-SNAPSHOT.jar..."
    sudo cp /opt/geo7/geo7-1.0-SNAPSHOT.jar "$BACKUP_DIR/" 2>/dev/null || echo "   Nenhum JAR anterior encontrado"
else
    echo "   Nenhum JAR anterior encontrado"
fi

# Backup do frontend (verificar qual diretório está sendo usado)
if [ -d "/home/ubuntu/geo7-web" ] && [ "$(ls -A /home/ubuntu/geo7-web 2>/dev/null)" ]; then
    echo "   Fazendo backup de /home/ubuntu/geo7-web..."
    sudo cp -r /home/ubuntu/geo7-web "$BACKUP_DIR/frontend_backup" 2>/dev/null || echo "   Nenhum frontend anterior encontrado em geo7-web"
elif [ -d "/var/www/html" ] && [ "$(ls -A /var/www/html 2>/dev/null)" ]; then
    echo "   Fazendo backup de /var/www/html..."
    sudo cp -r /var/www/html "$BACKUP_DIR/frontend_backup" 2>/dev/null || echo "   Nenhum frontend anterior encontrado em /var/www/html"
else
    echo "   Nenhum frontend anterior encontrado"
fi
echo "✅ Backup criado em $BACKUP_DIR"

# 3. Atualizar backend
echo ""
echo "3. Atualizando backend..."

# Verificar qual é o diretório usado pelo docker-compose
# O docker-compose pode usar /home/ubuntu/geo7-data ou /opt/geo7
JAR_DIR=""
if [ -d "/home/ubuntu/geo7-data" ]; then
    JAR_DIR="/home/ubuntu/geo7-data"
    echo "   ✅ Usando diretório: /home/ubuntu/geo7-data (docker-compose)"
elif [ -d "/opt/geo7" ]; then
    JAR_DIR="/opt/geo7"
    echo "   ✅ Usando diretório: /opt/geo7 (fallback)"
else
    # Criar diretório se não existir
    JAR_DIR="/home/ubuntu/geo7-data"
    echo "   ⚠️ Diretório não encontrado, criando: $JAR_DIR"
    sudo mkdir -p "$JAR_DIR"
fi

sudo cp geo7-1.0-SNAPSHOT.jar "$JAR_DIR/"
sudo chown root:root "$JAR_DIR/geo7-1.0-SNAPSHOT.jar"
sudo chmod 644 "$JAR_DIR/geo7-1.0-SNAPSHOT.jar"
echo "   ✅ JAR copiado para $JAR_DIR/geo7-1.0-SNAPSHOT.jar"
echo "   Tamanho: $(ls -lh "$JAR_DIR/geo7-1.0-SNAPSHOT.jar" | awk '{print $5}')"
echo "   Data: $(stat -c %y "$JAR_DIR/geo7-1.0-SNAPSHOT.jar" | cut -d' ' -f1,2 | cut -d'.' -f1)"
echo "✅ Backend atualizado"

# 4. Atualizar frontend
echo ""
echo "4. Atualizando frontend..."

# Verificar qual é o diretório usado pelo docker-compose
# O docker-compose pode usar /home/ubuntu/geo7-web ou /var/www/html
FRONTEND_DIR=""
if [ -d "/home/ubuntu/geo7-web" ]; then
    FRONTEND_DIR="/home/ubuntu/geo7-web"
    echo "   ✅ Usando diretório: /home/ubuntu/geo7-web (docker-compose)"
elif [ -d "/var/www/html" ]; then
    FRONTEND_DIR="/var/www/html"
    echo "   ✅ Usando diretório: /var/www/html (fallback)"
else
    # Criar diretório se não existir
    FRONTEND_DIR="/home/ubuntu/geo7-web"
    echo "   ⚠️ Diretório não encontrado, criando: $FRONTEND_DIR"
    sudo mkdir -p "$FRONTEND_DIR"
fi

echo "   Removendo arquivos antigos de $FRONTEND_DIR..."
sudo rm -rf "$FRONTEND_DIR"/*
echo "   Verificando estrutura do frontend..."

# Detectar onde está o index.html (pode estar em frontend/ ou frontend/browser/)
if [ -f "frontend/browser/index.html" ]; then
    echo "   ✅ Estrutura Angular detectada (browser/ subdiretório)"
    echo "   Copiando conteúdo de frontend/browser/ para $FRONTEND_DIR/..."
    sudo cp -r frontend/browser/* "$FRONTEND_DIR/"
elif [ -f "frontend/index.html" ]; then
    echo "   ✅ Estrutura padrão detectada"
    echo "   Copiando conteúdo de frontend/ para $FRONTEND_DIR/..."
    sudo cp -r frontend/* "$FRONTEND_DIR/"
else
    echo "   ⚠️ AVISO: index.html não encontrado em frontend/ nem frontend/browser/"
    echo "   Tentando copiar tudo mesmo assim..."
    sudo cp -r frontend/* "$FRONTEND_DIR/"
fi

# Ajustar permissões
sudo chown -R ubuntu:ubuntu "$FRONTEND_DIR" 2>/dev/null || sudo chown -R www-data:www-data "$FRONTEND_DIR"
sudo chmod -R 755 "$FRONTEND_DIR"

echo "   Verificando arquivos atualizados..."
if [ -f "$FRONTEND_DIR/index.html" ]; then
    echo "   ✅ index.html encontrado em $FRONTEND_DIR/"
    echo "   Data de modificação: $(stat -c %y "$FRONTEND_DIR/index.html" | cut -d' ' -f1,2 | cut -d'.' -f1)"
    echo "   Tamanho: $(ls -lh "$FRONTEND_DIR/index.html" | awk '{print $5}')"
    echo "   Primeiras linhas do index.html:"
    head -3 "$FRONTEND_DIR/index.html"
    
    # Verificar arquivos JS para confirmar versão
    MAIN_JS=$(find "$FRONTEND_DIR" -name "main-*.js" -type f 2>/dev/null | head -1)
    if [ -n "$MAIN_JS" ]; then
        echo "   ✅ Arquivo JS encontrado: $(basename $MAIN_JS)"
        echo "   Data: $(stat -c %y "$MAIN_JS" | cut -d' ' -f1,2 | cut -d'.' -f1)"
    fi
else
    echo "   ❌ ERRO: index.html não encontrado em $FRONTEND_DIR!"
    echo "   Conteúdo de $FRONTEND_DIR:"
    ls -la "$FRONTEND_DIR/" | head -10
    exit 1
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
