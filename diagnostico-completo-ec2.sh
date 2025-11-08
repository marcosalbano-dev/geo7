#!/bin/bash

# Script completo de diagnóstico na EC2
echo "=== DIAGNÓSTICO COMPLETO ==="
echo "Data: $(date)"
echo ""

# 1. Verificar build do frontend
echo "1. VERIFICANDO BUILD DO FRONTEND..."
echo ""
MAIN_JS=$(find /var/www/html -name "main*.js" -type f 2>/dev/null | head -1)
if [ -n "$MAIN_JS" ]; then
    echo "   Arquivo encontrado: $MAIN_JS"
    echo "   Tamanho: $(du -h "$MAIN_JS" | cut -f1)"
    echo ""
    echo "   Verificando URLs absolutas..."
    
    if grep -q "https://18-228-94-6.sslip.io" "$MAIN_JS" 2>/dev/null; then
        echo "   ❌ PROBLEMA ENCONTRADO: Build contém URLs absolutas HTTPS!"
        echo "   Isso causa o erro de tentar usar HTTPS quando deveria usar HTTP"
        echo ""
        echo "   Exemplos encontrados:"
        grep -o "https://18-228-94-6.sslip.io[^\"]*" "$MAIN_JS" 2>/dev/null | head -3
        BUILD_PROBLEMA=true
    elif grep -q "http://18-228-94-6.sslip.io" "$MAIN_JS" 2>/dev/null; then
        echo "   ⚠️ Build contém URLs absolutas HTTP"
        grep -o "http://18-228-94-6.sslip.io[^\"]*" "$MAIN_JS" 2>/dev/null | head -3
        BUILD_PROBLEMA=true
    else
        echo "   ✅ Build parece usar URLs relativas (correto)"
        BUILD_PROBLEMA=false
    fi
    
    echo ""
    echo "   Verificando se contém '/api' (URL relativa)..."
    if grep -q '"/api' "$MAIN_JS" 2>/dev/null || grep -q "'/api" "$MAIN_JS" 2>/dev/null; then
        echo "   ✅ Build contém '/api' (URL relativa encontrada)"
    else
        echo "   ⚠️ URL relativa '/api' não encontrada no build"
    fi
else
    echo "   ❌ Arquivo main.js não encontrado!"
    BUILD_PROBLEMA=true
fi

# 2. Verificar nginx.conf
echo ""
echo "2. VERIFICANDO NGINX.CONF..."
echo ""
NGINX_CONF=$(sudo docker exec geo7-web cat /etc/nginx/conf.d/default.conf 2>/dev/null)
if [ -n "$NGINX_CONF" ]; then
    echo "   ✅ nginx.conf encontrado"
    echo ""
    echo "   Configuração do proxy /api/:"
    echo "$NGINX_CONF" | grep -A 5 "location /api/" | head -6
    echo ""
    
    if echo "$NGINX_CONF" | grep -q "proxy_pass.*app:8080/api/"; then
        echo "   ✅ Proxy configurado corretamente (com /api/)"
    else
        echo "   ⚠️ Proxy pode precisar de ajuste"
    fi
else
    echo "   ❌ nginx.conf não encontrado no container"
fi

# 3. Testar conectividade
echo ""
echo "3. TESTANDO CONECTIVIDADE..."
echo ""
echo "   Testando backend diretamente..."
BACKEND_TEST=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/api/healthz 2>/dev/null)
echo "   Backend (porta 8080): HTTP $BACKEND_TEST"
echo ""

echo "   Testando proxy nginx..."
PROXY_TEST=$(curl -s -o /dev/null -w "%{http_code}" http://localhost/api/healthz 2>/dev/null)
echo "   Proxy (/api/healthz): HTTP $PROXY_TEST"
echo ""

if [ "$PROXY_TEST" = "200" ]; then
    echo "   ✅ Proxy funcionando!"
    RESPONSE=$(curl -s http://localhost/api/healthz)
    echo "   Resposta: $RESPONSE"
else
    echo "   ❌ Proxy não está funcionando (código: $PROXY_TEST)"
fi

# 4. Verificar logs do nginx
echo ""
echo "4. ÚLTIMOS LOGS DO NGINX..."
echo ""
sudo docker logs geo7-web --tail 10 2>/dev/null | grep -E "(error|warn|GET|POST)" | tail -5

# 5. Verificar docker-compose
echo ""
echo "5. VERIFICANDO DOCKER-COMPOSE..."
echo ""
if [ -f "docker-compose.yml" ]; then
    echo "   ✅ docker-compose.yml encontrado"
    echo "   Configuração do container web:"
    grep -A 15 "web:" docker-compose.yml | head -16
else
    echo "   ⚠️ docker-compose.yml não encontrado no diretório atual"
    DOCKER_COMPOSE=$(find ~ -name "docker-compose.yml" -type f 2>/dev/null | grep -v node_modules | head -1)
    if [ -n "$DOCKER_COMPOSE" ]; then
        echo "   Encontrado em: $DOCKER_COMPOSE"
    fi
fi

# 6. Resumo e recomendações
echo ""
echo "=== RESUMO ==="
echo ""

if [ "$BUILD_PROBLEMA" = true ]; then
    echo "❌ PROBLEMA IDENTIFICADO: Build do frontend contém URLs absolutas"
    echo ""
    echo "SOLUÇÃO:"
    echo "1. No seu computador local, execute:"
    echo "   cd geo7-app"
    echo "   npm run build:prod"
    echo "   (Isso garante que usa environment.prod.ts com apiUrl: '/api')"
    echo ""
    echo "2. Execute novamente:"
    echo "   .\atualizar-local-completo.ps1"
    echo "   .\upload-ec2.ps1"
    echo ""
    echo "3. Na EC2, execute:"
    echo "   ./atualizar-ec2.sh"
fi

if [ "$PROXY_TEST" != "200" ]; then
    echo "❌ PROBLEMA: Proxy do nginx não está funcionando"
    echo ""
    echo "SOLUÇÃO:"
    echo "1. Verificar se o container app está rodando:"
    echo "   sudo docker ps | grep geo7-app"
    echo ""
    echo "2. Verificar logs do app:"
    echo "   sudo docker logs geo7-app --tail 20"
    echo ""
    echo "3. Recarregar nginx:"
    echo "   sudo docker exec geo7-web nginx -s reload"
fi

echo ""
echo "=== DIAGNÓSTICO CONCLUÍDO ==="
echo ""
echo "⚠️ IMPORTANTE: Limpe o cache do navegador!"
echo "   - Chrome: Ctrl+Shift+Delete"
echo "   - Ou: chrome://net-internals/#hsts (remover domínio se estiver listado)"
echo "   - Ou use modo anônimo/privado"

