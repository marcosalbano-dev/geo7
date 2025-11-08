#!/bin/bash

# Diagnóstico final - verificar o que está acontecendo
echo "=== DIAGNÓSTICO FINAL ==="
echo "Data: $(date)"
echo ""

# 1. Verificar se há requisições chegando ao nginx
echo "1. Últimas requisições no nginx:"
sudo docker logs geo7-web --tail 50 | grep -E "(POST|GET|OPTIONS)" | tail -10
echo ""

# 2. Verificar se há requisições para /api/auth/login
echo "2. Requisições para /api/auth/login:"
sudo docker logs geo7-web --tail 100 | grep "/api/auth/login"
echo ""

# 3. Testar acesso externo
echo "3. Testando acesso externo:"
PUBLIC_IP=$(curl -s http://169.254.169.254/latest/meta-data/public-ipv4 2>/dev/null)
if [ -n "$PUBLIC_IP" ]; then
    echo "   IP Público: $PUBLIC_IP"
    echo "   Testando POST externo:"
    curl -v -X POST http://$PUBLIC_IP/api/auth/login \
      -H "Content-Type: application/json" \
      -H "Origin: http://$PUBLIC_IP" \
      -d '{"email":"test@test.com","password":"test"}' 2>&1 | grep -E "(< HTTP|Connection|refused)" | head -5
else
    echo "   Não foi possível obter IP público"
fi
echo ""

# 4. Verificar configuração do nginx atual
echo "4. Configuração atual do nginx (location /api/):"
sudo docker exec geo7-web cat /etc/nginx/conf.d/default.conf | grep -A 10 "location /api/"
echo ""

# 5. Verificar se o container está escutando na porta 80
echo "5. Verificando portas do container:"
sudo docker port geo7-web
echo ""

# 6. Verificar se há firewall bloqueando
echo "6. Verificando firewall:"
if command -v ufw >/dev/null 2>&1; then
    sudo ufw status | grep -E "(80|443)" || echo "   Portas 80/443 não encontradas nas regras"
fi
echo ""

# 7. Testar se o nginx está respondendo externamente
echo "7. Testando resposta do nginx externamente:"
if [ -n "$PUBLIC_IP" ]; then
    HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" --max-time 5 http://$PUBLIC_IP/ 2>/dev/null || echo "timeout")
    echo "   HTTP $PUBLIC_IP/ → $HTTP_CODE"
    
    API_CODE=$(curl -s -o /dev/null -w "%{http_code}" --max-time 5 http://$PUBLIC_IP/api/healthz 2>/dev/null || echo "timeout")
    echo "   HTTP $PUBLIC_IP/api/healthz → $API_CODE"
fi
echo ""

echo "=== DIAGNÓSTICO CONCLUÍDO ==="
echo ""
echo "⚠️ IMPORTANTE:"
echo "No navegador, abra DevTools (F12) -> Network e:"
echo "1. Tente fazer login"
echo "2. Veja qual URL EXATA está sendo chamada"
echo "3. Veja qual é o erro exato"
echo "4. Me envie um print da aba Network mostrando a requisição que falha"

