#!/bin/bash

# Script de diagnóstico completo

echo "=== DIAGNÓSTICO COMPLETO ==="
echo "Data: $(date)"
echo ""

# 1. Verificar docker-compose.yml atual
echo "1. Verificando docker-compose.yml atual:"
if [ -f "docker-compose.yml" ]; then
    echo "   ✅ docker-compose.yml encontrado"
    echo "   Configuração do nginx (portas):"
    grep -A 5 "nginx:" docker-compose.yml | grep -A 3 "ports:" || grep -A 5 "geo7-web:" docker-compose.yml | grep -A 3 "ports:"
else
    echo "   ❌ docker-compose.yml não encontrado"
fi
echo ""

# 2. Verificar nginx.conf atual
echo "2. Verificando nginx.conf:"
if [ -f "nginx.conf" ]; then
    echo "   ✅ nginx.conf encontrado"
    echo "   Configuração de listen:"
    grep -i "listen" nginx.conf | head -5
else
    echo "   ❌ nginx.conf não encontrado"
    echo "   Verificando no container..."
    sudo docker exec geo7-web cat /etc/nginx/nginx.conf 2>/dev/null | grep -i "listen" | head -5 || echo "   Não foi possível acessar"
fi
echo ""

# 3. Verificar qual nginx.conf está sendo usado
echo "3. Verificando qual nginx.conf está montado no container:"
echo ""
sudo docker inspect geo7-web | grep -A 10 "Mounts" | grep -i "nginx.conf" || echo "   Verificando volumes..."
sudo docker inspect geo7-web | grep -A 20 "Mounts"
echo ""

# 4. Testar acesso HTTP local
echo "4. Testando acesso HTTP local:"
echo ""
echo "   Teste 1: curl http://localhost/"
LOCAL_TEST=$(curl -s -o /dev/null -w "%{http_code}" http://localhost/ 2>/dev/null)
echo "   Status: $LOCAL_TEST"
echo ""

echo "   Teste 2: curl http://127.0.0.1/"
LOCAL_IP_TEST=$(curl -s -o /dev/null -w "%{http_code}" http://127.0.0.1/ 2>/dev/null)
echo "   Status: $LOCAL_IP_TEST"
echo ""

# 5. Verificar IP público e testar
echo "5. Verificando acesso externo:"
PUBLIC_IP=$(curl -s http://169.254.169.254/latest/meta-data/public-ipv4 2>/dev/null || echo "Não disponível")
echo "   IP Público: $PUBLIC_IP"
echo "   Tentando acessar http://$PUBLIC_IP/..."
PUBLIC_TEST=$(curl -s -o /dev/null -w "%{http_code}" --max-time 5 http://$PUBLIC_IP/ 2>/dev/null || echo "timeout")
echo "   Status: $PUBLIC_TEST"
echo ""

# 6. Verificar Security Groups (instruções)
echo "6. INSTRUÇÕES PARA VERIFICAR SECURITY GROUPS:"
echo ""
echo "   ⚠️ IMPORTANTE: Verifique no Console AWS se as Security Groups permitem:"
echo ""
echo "   1. Acesse: https://console.aws.amazon.com/ec2/"
echo "   2. Vá em 'Instances' e encontre sua instância"
echo "   3. Clique na instância e vá na aba 'Security'"
echo "   4. Clique no Security Group"
echo "   5. Verifique as regras 'Inbound rules':"
echo "      - Tipo: HTTP, Porta: 80, Origem: 0.0.0.0/0"
echo "      - Tipo: HTTPS, Porta: 443, Origem: 0.0.0.0/0"
echo ""
echo "   Se não existirem, adicione-as!"
echo ""

# 7. Verificar se está tentando HTTPS quando deveria ser HTTP
echo "7. Testando HTTP vs HTTPS:"
echo ""
echo "   Teste HTTP:"
HTTP_TEST=$(curl -s -o /dev/null -w "%{http_code}" --max-time 5 http://18-228-94-6.sslip.io/ 2>/dev/null || echo "timeout")
echo "   http://18-228-94-6.sslip.io → Status: $HTTP_TEST"
echo ""
echo "   Teste HTTPS:"
HTTPS_TEST=$(curl -s -k -o /dev/null -w "%{http_code}" --max-time 5 https://18-228-94-6.sslip.io/ 2>/dev/null || echo "timeout")
echo "   https://18-228-94-6.sslip.io → Status: $HTTPS_TEST"
echo ""

# 8. Verificar logs do nginx para erros
echo "8. Verificando erros no nginx:"
echo ""
sudo docker logs geo7-web 2>&1 | grep -i "error\|warn\|fail" | tail -10 || echo "   Nenhum erro encontrado"
echo ""

# 9. Verificar se o nginx está usando o arquivo correto
echo "9. Verificando configuração do nginx no container:"
echo ""
echo "   Testando configuração:"
sudo docker exec geo7-web nginx -t 2>&1
echo ""
echo "   Arquivo de configuração principal:"
sudo docker exec geo7-web cat /etc/nginx/nginx.conf 2>/dev/null | head -20
echo ""

# Resumo
echo "=== RESUMO ==="
echo ""
if [ "$LOCAL_TEST" = "200" ]; then
    echo "✅ Servidor funciona localmente"
else
    echo "❌ Servidor NÃO funciona localmente"
fi

if [ "$PUBLIC_TEST" = "200" ]; then
    echo "✅ Servidor acessível externamente"
elif [ "$PUBLIC_TEST" = "timeout" ]; then
    echo "❌ Servidor NÃO acessível externamente (timeout)"
    echo "   Isso indica problema de Security Groups ou firewall"
else
    echo "⚠️ Servidor respondeu com status: $PUBLIC_TEST"
fi

if [ "$HTTP_TEST" = "200" ]; then
    echo "✅ HTTP funciona"
else
    echo "❌ HTTP não funciona (status: $HTTP_TEST)"
fi

if [ "$HTTPS_TEST" = "200" ]; then
    echo "✅ HTTPS funciona"
elif [ "$HTTPS_TEST" != "timeout" ]; then
    echo "⚠️ HTTPS retornou status: $HTTPS_TEST"
fi

echo ""
echo "=== PRÓXIMOS PASSOS ==="
echo ""
if [ "$LOCAL_TEST" = "200" ] && [ "$PUBLIC_TEST" != "200" ]; then
    echo "🔧 PROBLEMA: Security Groups da AWS"
    echo "   - O servidor funciona localmente mas não externamente"
    echo "   - Verifique e corrija as Security Groups conforme instruções acima"
elif [ "$HTTP_TEST" != "200" ] && [ "$HTTPS_TEST" = "200" ]; then
    echo "🔧 PROBLEMA: Está tentando acessar HTTP mas só HTTPS funciona"
    echo "   - Use: https://18-228-94-6.sslip.io"
elif [ "$HTTP_TEST" = "200" ] && [ "$HTTPS_TEST" != "200" ]; then
    echo "🔧 PROBLEMA: Está tentando acessar HTTPS mas só HTTP funciona"
    echo "   - Use: http://18-228-94-6.sslip.io (sem 's')"
else
    echo "   Verifique os resultados acima e os logs do nginx"
fi
echo ""

