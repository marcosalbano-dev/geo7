#!/bin/bash

# Script para verificar e instruir sobre Security Groups da AWS
echo "=== VERIFICAÇÃO DE CONECTIVIDADE EXTERNA ==="
echo "Data: $(date)"
echo ""

# 1. Verificar se os containers estão rodando
echo "1. Verificando containers..."
if sudo docker ps | grep -q "geo7-web"; then
    echo "   ✅ Container geo7-web está rodando"
    PORTS=$(sudo docker ps | grep geo7-web | awk '{print $NF}')
    echo "   Portas mapeadas: $PORTS"
else
    echo "   ❌ Container geo7-web não está rodando"
    exit 1
fi

# 2. Testar acesso local
echo ""
echo "2. Testando acesso local..."
LOCAL_HTTP=$(curl -s -o /dev/null -w "%{http_code}" http://localhost/ 2>/dev/null)
LOCAL_API=$(curl -s -o /dev/null -w "%{http_code}" http://localhost/api/healthz 2>/dev/null)

echo "   HTTP local (porta 80): $LOCAL_HTTP"
echo "   API local (/api/healthz): $LOCAL_API"

if [ "$LOCAL_HTTP" = "200" ] && [ "$LOCAL_API" = "200" ]; then
    echo "   ✅ Tudo funciona localmente"
else
    echo "   ⚠️ Problemas locais detectados"
fi

# 3. Obter IP público
echo ""
echo "3. Obtendo informações de rede..."
PUBLIC_IP=$(curl -s http://169.254.169.254/latest/meta-data/public-ipv4 2>/dev/null)
INSTANCE_ID=$(curl -s http://169.254.169.254/latest/meta-data/instance-id 2>/dev/null)

if [ -n "$PUBLIC_IP" ]; then
    echo "   IP Público: $PUBLIC_IP"
    echo "   Instance ID: $INSTANCE_ID"
else
    echo "   ⚠️ Não foi possível obter IP público (pode não estar em EC2)"
fi

# 4. Testar acesso externo
echo ""
echo "4. Testando acesso externo..."
if [ -n "$PUBLIC_IP" ]; then
    EXTERNAL_HTTP=$(curl -s -o /dev/null -w "%{http_code}" --max-time 5 http://$PUBLIC_IP/ 2>/dev/null || echo "timeout")
    EXTERNAL_API=$(curl -s -o /dev/null -w "%{http_code}" --max-time 5 http://$PUBLIC_IP/api/healthz 2>/dev/null || echo "timeout")
    
    echo "   HTTP externo (http://$PUBLIC_IP/): $EXTERNAL_HTTP"
    echo "   API externa (http://$PUBLIC_IP/api/healthz): $EXTERNAL_API"
    
    if [ "$EXTERNAL_HTTP" = "200" ] && [ "$EXTERNAL_API" = "200" ]; then
        echo "   ✅ Acesso externo funcionando!"
    elif [ "$EXTERNAL_HTTP" = "timeout" ] || [ "$EXTERNAL_API" = "timeout" ]; then
        echo "   ❌ TIMEOUT - Security Groups provavelmente bloqueando"
    else
        echo "   ⚠️ Acesso externo retornou código: $EXTERNAL_HTTP / $EXTERNAL_API"
    fi
fi

# 5. Verificar firewall local
echo ""
echo "5. Verificando firewall local..."
if command -v ufw >/dev/null 2>&1; then
    UFW_STATUS=$(sudo ufw status | head -1)
    echo "   Status UFW: $UFW_STATUS"
    if echo "$UFW_STATUS" | grep -q "active"; then
        echo "   ⚠️ Firewall UFW está ATIVO - pode estar bloqueando portas"
        echo "   Verificando regras..."
        sudo ufw status | grep -E "(80|443)" || echo "   ⚠️ Portas 80/443 não encontradas nas regras UFW"
    fi
elif command -v iptables >/dev/null 2>&1; then
    echo "   Verificando iptables..."
    IPTABLES_80=$(sudo iptables -L -n | grep ":80" | head -1)
    IPTABLES_443=$(sudo iptables -L -n | grep ":443" | head -1)
    if [ -n "$IPTABLES_80" ] || [ -n "$IPTABLES_443" ]; then
        echo "   Regras encontradas para portas 80/443"
    fi
fi

# 6. Instruções para Security Groups
echo ""
echo "=== DIAGNÓSTICO ==="
echo ""
if [ "$LOCAL_HTTP" = "200" ] && [ "$EXTERNAL_HTTP" != "200" ]; then
    echo "❌ PROBLEMA IDENTIFICADO: Security Groups da AWS"
    echo ""
    echo "O servidor funciona localmente mas não externamente."
    echo "Isso indica que as Security Groups estão bloqueando o tráfego."
    echo ""
    echo "SOLUÇÃO:"
    echo ""
    echo "1. Acesse o Console AWS:"
    echo "   https://console.aws.amazon.com/ec2/"
    echo ""
    echo "2. Vá em 'Instances' e encontre sua instância:"
    if [ -n "$INSTANCE_ID" ]; then
        echo "   Instance ID: $INSTANCE_ID"
    fi
    echo ""
    echo "3. Clique na instância e vá na aba 'Security'"
    echo ""
    echo "4. Clique no Security Group"
    echo ""
    echo "5. Vá em 'Inbound rules' e verifique se existem estas regras:"
    echo ""
    echo "   Tipo: HTTP"
    echo "   Porta: 80"
    echo "   Origem: 0.0.0.0/0"
    echo ""
    echo "   Tipo: HTTPS"
    echo "   Porta: 443"
    echo "   Origem: 0.0.0.0/0"
    echo ""
    echo "6. Se não existirem, clique em 'Edit inbound rules' e adicione:"
    echo ""
    echo "   - Adicionar regra:"
    echo "     Tipo: HTTP"
    echo "     Porta: 80"
    echo "     Origem: 0.0.0.0/0"
    echo "     Descrição: Allow HTTP"
    echo ""
    echo "   - Adicionar regra:"
    echo "     Tipo: HTTPS"
    echo "     Porta: 443"
    echo "     Origem: 0.0.0.0/0"
    echo "     Descrição: Allow HTTPS"
    echo ""
    echo "7. Salve as alterações"
    echo ""
    echo "8. Aguarde alguns segundos e teste novamente:"
    echo "   http://$PUBLIC_IP"
    echo ""
elif [ "$LOCAL_HTTP" != "200" ]; then
    echo "❌ PROBLEMA: Servidor não funciona nem localmente"
    echo "   Verifique os logs: sudo docker logs geo7-web"
else
    echo "✅ Tudo parece estar funcionando!"
fi

echo ""
echo "=== VERIFICAÇÃO CONCLUÍDA ==="

