#!/bin/bash

# Script para verificar e corrigir configuração na EC2
echo "=== VERIFICANDO E CORRIGINDO CONFIGURAÇÃO NA EC2 ==="
echo "Data: $(date)"
echo ""

# 1. Verificar docker-compose.yml
echo "1. Verificando docker-compose.yml..."
if [ -f "docker-compose.yml" ]; then
    echo "   ✅ docker-compose.yml encontrado"
    echo "   Verificando configuração do container web..."
    
    # Verificar se está usando build ou volume
    if grep -q "build:" docker-compose.yml; then
        echo "   ⚠️ Container web está usando BUILD (precisa reconstruir)"
        NEED_REBUILD=true
    elif grep -q "volumes:" docker-compose.yml; then
        echo "   ✅ Container web está usando VOLUMES"
        NEED_REBUILD=false
    fi
else
    echo "   ❌ docker-compose.yml não encontrado no diretório atual"
    echo "   Procurando em outros locais..."
    find ~ -name "docker-compose.yml" -type f 2>/dev/null | head -3
fi

# 2. Verificar nginx.conf no container
echo ""
echo "2. Verificando nginx.conf no container..."
if sudo docker exec geo7-web test -f /etc/nginx/conf.d/default.conf 2>/dev/null; then
    echo "   ✅ Arquivo de configuração encontrado"
    echo "   Verificando configuração do proxy..."
    
    # Verificar se o proxy está correto
    PROXY_CONFIG=$(sudo docker exec geo7-web cat /etc/nginx/conf.d/default.conf 2>/dev/null | grep -A 5 "location /api/")
    
    if echo "$PROXY_CONFIG" | grep -q "proxy_pass.*app:8080/api/"; then
        echo "   ✅ Proxy configurado corretamente (com /api/)"
    elif echo "$PROXY_CONFIG" | grep -q "proxy_pass.*app:8080"; then
        echo "   ⚠️ Proxy pode precisar de ajuste"
        echo "   Configuração atual:"
        echo "$PROXY_CONFIG"
    else
        echo "   ❌ Proxy não encontrado ou incorreto"
    fi
else
    echo "   ⚠️ Arquivo de configuração não encontrado no caminho padrão"
    echo "   Verificando outros caminhos..."
    sudo docker exec geo7-web find /etc/nginx -name "*.conf" -type f 2>/dev/null
fi

# 3. Verificar build do frontend
echo ""
echo "3. Verificando build do frontend..."
if [ -f "/var/www/html/index.html" ]; then
    echo "   ✅ index.html encontrado"
    
    # Verificar se o build usa URL relativa (não deve ter http:// ou https://)
    echo "   Verificando se o build usa URL relativa..."
    
    # Procurar por URLs absolutas no main.js
    MAIN_JS=$(find /var/www/html -name "main*.js" -type f | head -1)
    if [ -n "$MAIN_JS" ]; then
        echo "   Arquivo JavaScript encontrado: $MAIN_JS"
        
        if grep -q "https://18-228-94-6.sslip.io" "$MAIN_JS" 2>/dev/null; then
            echo "   ❌ PROBLEMA ENCONTRADO: Build contém URLs absolutas HTTPS!"
            echo "   Isso causa o erro de tentar usar HTTPS quando deveria usar HTTP"
            echo "   Solução: Rebuild do frontend com 'npm run build:prod'"
        elif grep -q "http://18-228-94-6.sslip.io" "$MAIN_JS" 2>/dev/null; then
            echo "   ⚠️ Build contém URLs absolutas HTTP (pode funcionar, mas não é ideal)"
        else
            echo "   ✅ Build parece usar URLs relativas (correto)"
        fi
    else
        echo "   ⚠️ Arquivo main.js não encontrado"
    fi
else
    echo "   ❌ index.html não encontrado em /var/www/html"
fi

# 4. Verificar se o container precisa ser reconstruído
echo ""
echo "4. Verificando se precisa reconstruir container web..."
if [ "$NEED_REBUILD" = true ]; then
    echo "   ⚠️ Container web usa BUILD - precisa reconstruir com novo nginx.conf"
    echo "   Execute:"
    echo "   sudo docker-compose build web"
    echo "   sudo docker-compose up -d web"
else
    echo "   ✅ Container web usa VOLUMES - nginx.conf deve estar montado"
    
    # Verificar se há nginx.conf no diretório atual
    if [ -f "nginx.conf" ]; then
        echo "   ✅ nginx.conf encontrado no diretório atual"
        echo "   Verificando se está montado no docker-compose..."
        
        if grep -q "nginx.conf" docker-compose.yml; then
            echo "   ✅ nginx.conf está configurado no docker-compose.yml"
        else
            echo "   ⚠️ nginx.conf não está configurado no docker-compose.yml"
        fi
    else
        echo "   ⚠️ nginx.conf não encontrado no diretório atual"
    fi
fi

# 5. Testar proxy
echo ""
echo "5. Testando proxy do nginx..."
echo "   Testando: curl http://localhost/api/healthz"
PROXY_TEST=$(curl -s -o /dev/null -w "%{http_code}" http://localhost/api/healthz 2>/dev/null)
if [ "$PROXY_TEST" = "200" ]; then
    echo "   ✅ Proxy funcionando corretamente (HTTP 200)"
else
    echo "   ⚠️ Proxy retornou código: $PROXY_TEST"
    echo "   Testando backend diretamente..."
    BACKEND_TEST=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/api/healthz 2>/dev/null)
    if [ "$BACKEND_TEST" = "200" ]; then
        echo "   ✅ Backend funciona diretamente, mas proxy pode ter problema"
    else
        echo "   ❌ Backend também não responde"
    fi
fi

# 6. Resumo e recomendações
echo ""
echo "=== RESUMO E RECOMENDAÇÕES ==="
echo ""
echo "Se o problema persistir:"
echo ""
echo "1. Reconstruir container web (se usar BUILD):"
echo "   sudo docker-compose build web"
echo "   sudo docker-compose up -d web"
echo ""
echo "2. Verificar nginx.conf no container:"
echo "   sudo docker exec geo7-web cat /etc/nginx/conf.d/default.conf"
echo ""
echo "3. Recarregar nginx sem reiniciar:"
echo "   sudo docker exec geo7-web nginx -s reload"
echo ""
echo "4. Verificar logs do nginx:"
echo "   sudo docker logs geo7-web"
echo ""
echo "5. Limpar cache do navegador:"
echo "   - Chrome: Ctrl+Shift+Delete ou chrome://net-internals/#hsts"
echo "   - Remover domínio 18-228-94-6.sslip.io se estiver em HSTS"
echo ""
echo "✅ Verificação concluída!"

