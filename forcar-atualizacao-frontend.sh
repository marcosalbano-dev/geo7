#!/bin/bash

# Script para FORÇAR atualização do frontend na EC2
# Use quando o frontend não atualizar mesmo após seguir o processo normal

echo "=== FORÇANDO ATUALIZAÇÃO DO FRONTEND ==="
echo "Data: $(date)"
echo ""

# Verificar se o diretório frontend existe
if [ ! -d "frontend" ]; then
    echo "❌ Diretório frontend não encontrado no diretório atual"
    echo "   Certifique-se de estar no diretório ~ (home) e que o frontend foi enviado"
    exit 1
fi

# 1. Parar containers
echo "1. Parando containers..."
sudo docker-compose down
sleep 2

# 2. Remover COMPLETAMENTE o diretório do frontend
echo ""
echo "2. Removendo completamente arquivos antigos do frontend..."
sudo rm -rf /var/www/html/*
sudo rm -rf /var/www/html/.* 2>/dev/null || true
echo "   ✅ Diretório limpo completamente"

# 3. Verificar estrutura do frontend local
echo ""
echo "3. Verificando estrutura do frontend local..."
echo "   Arquivos no diretório frontend:"
ls -la frontend/ | head -10
echo ""

# Detectar onde está o index.html (pode estar em frontend/ ou frontend/browser/)
INDEX_PATH=""
if [ -f "frontend/index.html" ]; then
    INDEX_PATH="frontend/index.html"
    FRONTEND_SOURCE="frontend"
    echo "   ✅ index.html encontrado em frontend/"
elif [ -f "frontend/browser/index.html" ]; then
    INDEX_PATH="frontend/browser/index.html"
    FRONTEND_SOURCE="frontend/browser"
    echo "   ✅ index.html encontrado em frontend/browser/"
    echo "   (Estrutura Angular com subdiretório browser/)"
else
    echo "   ⚠️ AVISO: index.html não encontrado!"
    echo "   Verificando estrutura..."
    find frontend -name "index.html" -type f 2>/dev/null | head -5
    INDEX_PATH=$(find frontend -name "index.html" -type f 2>/dev/null | head -1)
    if [ -n "$INDEX_PATH" ]; then
        FRONTEND_SOURCE=$(dirname "$INDEX_PATH")
        echo "   ✅ index.html encontrado em: $FRONTEND_SOURCE"
    else
        echo "   ❌ ERRO: index.html não encontrado em lugar nenhum!"
        exit 1
    fi
fi

if [ -n "$INDEX_PATH" ]; then
    echo "   Primeiras linhas do index.html:"
    head -5 "$INDEX_PATH"
fi

# 4. Copiar arquivos com verificação
echo ""
echo "4. Copiando novos arquivos..."
echo "   Fonte: $FRONTEND_SOURCE"
echo "   Destino: /var/www/html/"
if [ "$FRONTEND_SOURCE" = "frontend/browser" ]; then
    # Se está em browser/, copiar o CONTEÚDO de browser/ para a raiz
    echo "   Copiando conteúdo de browser/ para raiz do html..."
    sudo cp -rv "$FRONTEND_SOURCE"/* /var/www/html/ 2>&1 | head -20
else
    # Se está na raiz, copiar normalmente
    sudo cp -rv frontend/* /var/www/html/ 2>&1 | head -20
fi
echo ""

# 5. Ajustar permissões
echo "5. Ajustando permissões..."
sudo chown -R www-data:www-data /var/www/html
sudo chmod -R 755 /var/www/html

# 6. Verificar arquivos copiados
echo ""
echo "6. Verificando arquivos copiados..."
if [ -f "/var/www/html/index.html" ]; then
    echo "   ✅ index.html encontrado no destino"
    echo "   Tamanho do arquivo:"
    ls -lh /var/www/html/index.html
    echo "   Primeiras linhas do index.html no destino:"
    head -5 /var/www/html/index.html
    echo ""
    echo "   Comparação de hash (MD5):"
    if [ -n "$INDEX_PATH" ]; then
        echo "   Local:  $(md5sum "$INDEX_PATH" 2>/dev/null | cut -d' ' -f1 || echo 'N/A')"
    else
        echo "   Local:  N/A"
    fi
    echo "   Destino: $(md5sum /var/www/html/index.html 2>/dev/null | cut -d' ' -f1 || echo 'N/A')"
else
    echo "   ❌ ERRO: index.html NÃO encontrado no destino!"
    echo "   Arquivos em /var/www/html:"
    ls -la /var/www/html/ | head -10
    exit 1
fi

# 7. Reiniciar containers
echo ""
echo "7. Reiniciando containers..."
sudo docker-compose up -d

if [ $? -eq 0 ]; then
    echo "   ✅ Containers reiniciados"
else
    echo "   ❌ Erro ao reiniciar containers"
    exit 1
fi

# 8. Aguardar inicialização
echo ""
echo "8. Aguardando inicialização..."
sleep 15

# 9. Forçar reload do nginx
echo ""
echo "9. Forçando reload do nginx..."
sudo docker exec geo7-web nginx -s reload 2>/dev/null && echo "   ✅ Nginx recarregado" || echo "   ⚠️ Nginx pode não ter recarregado (mas deve estar OK)"

# 10. Verificar status
echo ""
echo "10. Verificando status..."
sudo docker ps --format "table {{.Names}}\t{{.Status}}"

# 11. Testar endpoints
echo ""
echo "11. Testando endpoints..."
echo "   Backend health check:"
if curl -s http://localhost:8080/api/healthz > /dev/null; then
    echo "   ✅ Backend respondendo"
else
    echo "   ⚠️ Backend não está respondendo (pode estar iniciando)"
fi

echo ""
echo "   Frontend:"
if curl -s http://localhost/ | grep -q "geo7\|Geo7\|angular" > /dev/null; then
    echo "   ✅ Frontend respondendo"
else
    echo "   ⚠️ Frontend pode não estar respondendo corretamente"
fi

# Resumo
echo ""
echo "=== ATUALIZAÇÃO FORÇADA CONCLUÍDA ==="
echo ""
echo "✅ Frontend atualizado e reiniciado"
echo ""
echo "⚠️ IMPORTANTE - Próximos passos:"
echo "1. Limpe o cache do navegador:"
echo "   - Chrome/Edge: Ctrl+Shift+R (Windows) ou Cmd+Shift+R (Mac)"
echo "   - Firefox: Ctrl+F5 (Windows) ou Cmd+Shift+R (Mac)"
echo "   - Ou abra em modo anônimo/privado"
echo ""
echo "2. Acesse: https://18-228-94-6.sslip.io"
echo ""
echo "3. Se ainda houver problemas, verifique:"
echo "   sudo docker logs geo7-web"
echo "   ls -la /var/www/html/"
echo ""

