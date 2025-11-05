#!/bin/bash

# Script para verificar se a atualização foi aplicada corretamente
echo "=== VERIFICANDO ATUALIZAÇÃO DO SISTEMA ==="
echo "Data: $(date)"
echo ""

# 1. Verificar arquivos no servidor
echo "1. Verificando arquivos no servidor..."
echo ""
echo "   Arquivos em /var/www/html:"
ls -lah /var/www/html/ | head -15
echo ""

if [ -f "/var/www/html/index.html" ]; then
    echo "   ✅ index.html encontrado"
    echo "   Tamanho: $(ls -lh /var/www/html/index.html | awk '{print $5}')"
    echo "   Data de modificação: $(stat -c %y /var/www/html/index.html | cut -d' ' -f1,2 | cut -d'.' -f1)"
    echo ""
    echo "   Primeiras 10 linhas do index.html:"
    head -10 /var/www/html/index.html
    echo ""
else
    echo "   ❌ index.html NÃO encontrado!"
fi

# 2. Verificar arquivos JS principais
echo "2. Verificando arquivos JavaScript principais..."
echo ""
JS_FILES=$(find /var/www/html -name "main-*.js" -type f 2>/dev/null | head -3)
if [ -n "$JS_FILES" ]; then
    echo "   Arquivos JS encontrados:"
    for js in $JS_FILES; do
        echo "   - $js"
        echo "     Tamanho: $(ls -lh "$js" | awk '{print $5}')"
        echo "     Modificado: $(stat -c %y "$js" | cut -d' ' -f1,2 | cut -d'.' -f1)"
    done
else
    echo "   ⚠️ Nenhum arquivo JS main-*.js encontrado"
fi
echo ""

# 3. Verificar containers
echo "3. Verificando status dos containers..."
echo ""
sudo docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
echo ""

# 4. Verificar health check
echo "4. Verificando health check do backend..."
echo ""
if curl -s http://localhost:8080/api/healthz > /dev/null; then
    echo "   ✅ Backend respondendo"
    curl -s http://localhost:8080/api/healthz
    echo ""
else
    echo "   ⚠️ Backend não está respondendo"
fi
echo ""

# 5. Verificar frontend
echo "5. Verificando frontend..."
echo ""
FRONTEND_RESPONSE=$(curl -s http://localhost/ | head -20)
if echo "$FRONTEND_RESPONSE" | grep -q "geo7\|Geo7\|angular\|app-root"; then
    echo "   ✅ Frontend respondendo"
    echo "   Primeiras linhas da resposta:"
    echo "$FRONTEND_RESPONSE" | head -5
else
    echo "   ⚠️ Frontend pode não estar respondendo corretamente"
    echo "   Resposta:"
    echo "$FRONTEND_RESPONSE" | head -10
fi
echo ""

# 6. Verificar hash dos arquivos principais
echo "6. Verificando hash dos arquivos (para detectar mudanças)..."
echo ""
if [ -f "/var/www/html/index.html" ]; then
    echo "   Hash MD5 do index.html:"
    md5sum /var/www/html/index.html | cut -d' ' -f1
fi
if [ -n "$JS_FILES" ]; then
    FIRST_JS=$(echo "$JS_FILES" | head -1)
    echo "   Hash MD5 do primeiro JS:"
    md5sum "$FIRST_JS" | cut -d' ' -f1
fi
echo ""

# 7. Verificar logs recentes do nginx
echo "7. Últimas requisições do nginx (últimos 5 acessos):"
echo ""
sudo docker logs geo7-web --tail 5 2>/dev/null || echo "   (Não foi possível acessar logs)"
echo ""

# 8. Instruções finais
echo "=== RESUMO DA VERIFICAÇÃO ==="
echo ""
echo "Se os arquivos parecem estar atualizados mas o navegador ainda mostra versão antiga:"
echo "1. Limpe o cache do navegador: Ctrl+Shift+R (Windows) ou Cmd+Shift+R (Mac)"
echo "2. Ou abra em modo anônimo/privado"
echo "3. Ou limpe o cache completamente:"
echo "   Chrome: Configurações > Privacidade > Limpar dados de navegação"
echo "   Firefox: Configurações > Privacidade > Limpar dados"
echo ""
echo "Para forçar atualização completa, execute:"
echo "  ./forcar-atualizacao-frontend.sh"
echo ""


