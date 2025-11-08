#!/bin/bash

echo "=== VERIFICANDO CONFIGURAÇÃO HTTPS ==="
echo ""

# 1. Verificar certificados
echo "1. Verificando certificados SSL..."
if [ -f "/etc/ssl/geo7/fullchain.pem" ] && [ -f "/etc/ssl/geo7/privkey.pem" ]; then
    echo "✅ Certificados encontrados em /etc/ssl/geo7/"
    ls -lh /etc/ssl/geo7/
else
    echo "❌ Certificados NÃO encontrados!"
    exit 1
fi

echo ""
echo "2. Verificando docker-compose.yml..."
if [ -f "docker-compose.yml" ]; then
    echo "✅ docker-compose.yml encontrado"
    
    # Verificar se monta o volume SSL
    if grep -q "/etc/ssl/geo7" docker-compose.yml; then
        echo "✅ Volume SSL configurado no docker-compose.yml"
        grep "/etc/ssl/geo7" docker-compose.yml
    else
        echo "⚠️ Volume SSL NÃO encontrado no docker-compose.yml"
        echo ""
        echo "Você precisa adicionar ao serviço 'web' ou 'nginx':"
        echo "  volumes:"
        echo "    - /etc/ssl/geo7:/etc/ssl/geo7:ro"
    fi
    
    # Verificar se porta 443 está exposta
    if grep -q "443:443" docker-compose.yml; then
        echo "✅ Porta 443 configurada no docker-compose.yml"
    else
        echo "⚠️ Porta 443 NÃO encontrada no docker-compose.yml"
        echo "Você precisa adicionar:"
        echo "  ports:"
        echo "    - \"80:80\""
        echo "    - \"443:443\""
    fi
else
    echo "❌ docker-compose.yml NÃO encontrado no diretório atual"
fi

echo ""
echo "3. Verificando container nginx..."
if sudo docker ps | grep -q "geo7-web\|nginx"; then
    CONTAINER_NAME=$(sudo docker ps | grep -E "geo7-web|nginx" | awk '{print $NF}' | head -1)
    echo "✅ Container encontrado: $CONTAINER_NAME"
    
    # Verificar se os certificados estão acessíveis dentro do container
    echo ""
    echo "4. Verificando acesso aos certificados dentro do container..."
    if sudo docker exec $CONTAINER_NAME ls /etc/ssl/geo7/fullchain.pem > /dev/null 2>&1; then
        echo "✅ Certificados acessíveis dentro do container"
        sudo docker exec $CONTAINER_NAME ls -lh /etc/ssl/geo7/
    else
        echo "❌ Certificados NÃO acessíveis dentro do container"
        echo "O volume não está montado corretamente!"
    fi
    
    # Verificar configuração do nginx
    echo ""
    echo "5. Verificando configuração do nginx..."
    if sudo docker exec $CONTAINER_NAME cat /etc/nginx/conf.d/default.conf | grep -q "ssl_certificate"; then
        echo "✅ Configuração SSL encontrada no nginx.conf"
        echo ""
        echo "Linhas relevantes:"
        sudo docker exec $CONTAINER_NAME cat /etc/nginx/conf.d/default.conf | grep -A 2 "ssl_certificate"
    else
        echo "⚠️ Configuração SSL NÃO encontrada no nginx.conf"
    fi
    
    # Testar nginx config
    echo ""
    echo "6. Testando configuração do nginx..."
    if sudo docker exec $CONTAINER_NAME nginx -t 2>&1 | grep -q "successful"; then
        echo "✅ Configuração do nginx está válida"
    else
        echo "❌ Erro na configuração do nginx:"
        sudo docker exec $CONTAINER_NAME nginx -t
    fi
else
    echo "⚠️ Container nginx não está rodando"
fi

echo ""
echo "=== RESUMO ==="
echo "Se todos os itens acima estão ✅, o HTTPS deve funcionar!"
echo ""
echo "Para testar HTTPS:"
echo "  curl -k https://18-228-94-6.sslip.io"
echo ""
echo "Para reiniciar o nginx (se necessário):"
echo "  sudo docker exec $CONTAINER_NAME nginx -s reload"

