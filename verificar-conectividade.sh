#!/bin/bash

# Script para verificar conectividade externa

echo "=== VERIFICANDO CONECTIVIDADE EXTERNA ==="
echo "Data: $(date)"
echo ""

# 1. Verificar IP público
echo "1. IP Público da instância:"
PUBLIC_IP=$(curl -s http://169.254.169.254/latest/meta-data/public-ipv4 2>/dev/null || echo "Não disponível")
echo "   IP: $PUBLIC_IP"
echo ""

# 2. Testar HTTP localmente
echo "2. Testando HTTP localmente (localhost):"
LOCAL_HTTP=$(curl -s -o /dev/null -w "%{http_code}" http://localhost/ 2>/dev/null)
echo "   Status: $LOCAL_HTTP"
if [ "$LOCAL_HTTP" = "200" ]; then
    echo "   ✅ HTTP local funcionando"
else
    echo "   ❌ HTTP local não está funcionando"
fi
echo ""

# 3. Testar HTTP pelo IP interno
echo "3. Testando HTTP pelo IP interno:"
INTERNAL_IP=$(hostname -I | awk '{print $1}')
echo "   IP interno: $INTERNAL_IP"
INTERNAL_HTTP=$(curl -s -o /dev/null -w "%{http_code}" http://$INTERNAL_IP/ 2>/dev/null)
echo "   Status: $INTERNAL_HTTP"
if [ "$INTERNAL_HTTP" = "200" ]; then
    echo "   ✅ HTTP pelo IP interno funcionando"
else
    echo "   ❌ HTTP pelo IP interno não está funcionando"
fi
echo ""

# 4. Verificar se o nginx está escutando em 0.0.0.0
echo "4. Verificando em quais interfaces o nginx está escutando:"
echo ""
sudo netstat -tlnp | grep :80 | grep nginx || echo "   Nginx não encontrado no netstat"
echo ""

# 5. Verificar configuração do nginx
echo "5. Verificando configuração do nginx:"
if sudo docker exec geo7-web cat /etc/nginx/nginx.conf 2>/dev/null | grep -q "listen.*80"; then
    echo "   ✅ Nginx configurado para escutar na porta 80"
    sudo docker exec geo7-web cat /etc/nginx/nginx.conf 2>/dev/null | grep "listen" | head -5
else
    echo "   ⚠️ Não foi possível verificar configuração do nginx"
fi
echo ""

# 6. Verificar Security Groups (se AWS CLI estiver disponível)
echo "6. Verificando Security Groups (se AWS CLI disponível):"
if command -v aws &> /dev/null; then
    echo "   AWS CLI disponível"
    # Tentar verificar security groups (pode não funcionar sem permissões)
    INSTANCE_ID=$(curl -s http://169.254.169.254/latest/meta-data/instance-id 2>/dev/null)
    if [ -n "$INSTANCE_ID" ]; then
        echo "   Instance ID: $INSTANCE_ID"
        echo "   ⚠️ Verifique manualmente no Console AWS se as portas 80 e 443 estão abertas"
    fi
else
    echo "   AWS CLI não disponível"
    echo "   ⚠️ IMPORTANTE: Verifique no Console AWS se as Security Groups permitem:"
    echo "      - Porta 80 (HTTP) de 0.0.0.0/0"
    echo "      - Porta 443 (HTTPS) de 0.0.0.0/0"
fi
echo ""

# 7. Testar curl do próprio servidor para o domínio
echo "7. Testando acesso ao domínio do próprio servidor:"
DOMAIN_HTTP=$(curl -s -o /dev/null -w "%{http_code}" http://18-228-94-6.sslip.io/ 2>/dev/null)
echo "   Status: $DOMAIN_HTTP"
if [ "$DOMAIN_HTTP" = "200" ]; then
    echo "   ✅ Domínio acessível do servidor"
else
    echo "   ⚠️ Domínio pode não estar acessível externamente"
fi
echo ""

# 8. Verificar se há firewall local
echo "8. Verificando firewall local:"
if command -v ufw &> /dev/null; then
    echo "   Status UFW:"
    sudo ufw status | head -10
elif command -v firewall-cmd &> /dev/null; then
    echo "   Status firewalld:"
    sudo firewall-cmd --list-all 2>/dev/null | head -10
else
    echo "   Nenhum firewall local detectado"
fi
echo ""

# 9. Verificar logs recentes do nginx para requisições
echo "9. Últimas requisições no nginx (últimos 5 minutos):"
echo ""
sudo docker logs geo7-web --since 5m 2>/dev/null | grep -i "GET\|POST\|error" | tail -10 || echo "   Nenhuma requisição recente"
echo ""

# Resumo
echo "=== RESUMO E DIAGNÓSTICO ==="
echo ""
echo "Se o servidor responde localmente mas não externamente:"
echo "1. Verifique Security Groups na AWS EC2:"
echo "   - Porta 80 (HTTP) deve permitir tráfego de 0.0.0.0/0"
echo "   - Porta 443 (HTTPS) deve permitir tráfego de 0.0.0.0/0"
echo ""
echo "2. Verifique se está tentando acessar via HTTPS:"
echo "   - Tente http://18-228-94-6.sslip.io (sem 's' no http)"
echo "   - Ou verifique se o certificado SSL está configurado"
echo ""
echo "3. Verifique DNS:"
echo "   - O domínio 18-228-94-6.sslip.io deve apontar para o IP público"
echo ""
echo "4. Teste de outro lugar:"
echo "   - Tente acessar de outro computador/rede"
echo "   - Ou use um serviço como https://downforeveryoneorjustme.com/"
echo ""

