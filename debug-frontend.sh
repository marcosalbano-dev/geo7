#!/bin/bash

# Script para debugar o problema do frontend não atualizar

echo "=== DEBUG: VERIFICANDO FRONTEND NA EC2 ==="
echo "Data: $(date)"
echo ""

# 1. Verificar arquivos no diretório de upload
echo "1. Arquivos no diretório ~/frontend:"
if [ -d "frontend" ]; then
    ls -lah frontend/ | head -15
    echo ""
    if [ -f "frontend/index.html" ]; then
        echo "   ✅ index.html encontrado em ~/frontend/"
        echo "   Data: $(stat -c %y frontend/index.html | cut -d' ' -f1,2 | cut -d'.' -f1)"
        echo "   Tamanho: $(ls -lh frontend/index.html | awk '{print $5}')"
        echo "   Primeiras 5 linhas:"
        head -5 frontend/index.html
    else
        echo "   ❌ index.html NÃO encontrado em ~/frontend/"
        echo "   Verificando estrutura..."
        find frontend -name "index.html" -type f 2>/dev/null
    fi
else
    echo "   ❌ Diretório frontend não existe!"
fi
echo ""

# 2. Verificar arquivos no diretório do nginx
echo "2. Arquivos em /var/www/html:"
ls -lah /var/www/html/ | head -15
echo ""
if [ -f "/var/www/html/index.html" ]; then
    echo "   ✅ index.html encontrado em /var/www/html/"
    echo "   Data: $(stat -c %y /var/www/html/index.html | cut -d' ' -f1,2 | cut -d'.' -f1)"
    echo "   Tamanho: $(ls -lh /var/www/html/index.html | awk '{print $5}')"
    echo "   Hash MD5: $(md5sum /var/www/html/index.html | cut -d' ' -f1)"
    echo "   Primeiras 5 linhas:"
    head -5 /var/www/html/index.html
else
    echo "   ❌ index.html NÃO encontrado em /var/www/html/"
    echo "   Verificando estrutura..."
    find /var/www/html -name "index.html" -type f 2>/dev/null
fi
echo ""

# 3. Comparar hashes se ambos existirem
if [ -f "frontend/index.html" ] && [ -f "/var/www/html/index.html" ]; then
    echo "3. Comparando arquivos:"
    HASH_FRONTEND=$(md5sum frontend/index.html | cut -d' ' -f1)
    HASH_WWW=$(md5sum /var/www/html/index.html | cut -d' ' -f1)
    echo "   Hash de ~/frontend/index.html: $HASH_FRONTEND"
    echo "   Hash de /var/www/html/index.html: $HASH_WWW"
    if [ "$HASH_FRONTEND" = "$HASH_WWW" ]; then
        echo "   ✅ Arquivos são idênticos"
    else
        echo "   ⚠️ Arquivos são DIFERENTES!"
    fi
fi
echo ""

# 4. Verificar arquivos JS principais
echo "4. Verificando arquivos JavaScript principais:"
JS_FILES=$(find /var/www/html -name "main-*.js" -type f 2>/dev/null | head -3)
if [ -n "$JS_FILES" ]; then
    for js in $JS_FILES; do
        echo "   - $(basename $js)"
        echo "     Data: $(stat -c %y "$js" | cut -d' ' -f1,2 | cut -d'.' -f1)"
        echo "     Tamanho: $(ls -lh "$js" | awk '{print $5}')"
        echo "     Hash: $(md5sum "$js" | cut -d' ' -f1)"
    done
else
    echo "   ⚠️ Nenhum arquivo main-*.js encontrado"
fi
echo ""

# 5. Verificar o que o nginx está servindo
echo "5. Testando o que o nginx está servindo:"
echo "   Testando http://localhost/"
RESPONSE=$(curl -s http://localhost/ | head -20)
if echo "$RESPONSE" | grep -q "geo7\|Geo7\|angular\|app-root"; then
    echo "   ✅ Nginx está servindo conteúdo Angular"
    echo "   Primeiras 10 linhas da resposta:"
    echo "$RESPONSE" | head -10
else
    echo "   ⚠️ Nginx pode não estar servindo conteúdo correto"
    echo "   Resposta:"
    echo "$RESPONSE" | head -10
fi
echo ""

# 6. Verificar se há cache do nginx
echo "6. Verificando cache do nginx:"
echo "   Reload do nginx (força limpar cache interno)..."
sudo docker exec geo7-web nginx -s reload 2>/dev/null && echo "   ✅ Nginx recarregado" || echo "   ⚠️ Erro ao recarregar nginx"
echo ""

# 7. Verificar permissões
echo "7. Verificando permissões:"
ls -ld /var/www/html
ls -ld /var/www/html/index.html 2>/dev/null || echo "   index.html não encontrado"
echo ""

# 8. Instruções finais
echo "=== RESUMO ==="
echo ""
echo "Se os arquivos estão corretos mas o navegador ainda mostra versão antiga:"
echo "1. Limpe o cache do navegador: Ctrl+Shift+R (Windows) ou Cmd+Shift+R (Mac)"
echo "2. Ou abra em modo anônimo/privado"
echo "3. Ou limpe o cache completamente nas configurações do navegador"
echo ""
echo "Para forçar atualização completa novamente:"
echo "  ./forcar-atualizacao-frontend.sh"
echo ""

