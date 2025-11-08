#!/bin/bash

# Script para verificar status completo da EC2

echo "=== VERIFICANDO STATUS DA EC2 ==="
echo "Data: $(date)"
echo ""

# 1. Verificar containers
echo "1. Status dos containers Docker:"
echo ""
sudo docker ps -a
echo ""

# 2. Verificar logs do nginx
echo "2. Últimas 20 linhas dos logs do nginx:"
echo ""
sudo docker logs geo7-web --tail 20 2>/dev/null || echo "   Container geo7-web não está rodando"
echo ""

# 3. Verificar logs do app
echo "3. Últimas 10 linhas dos logs do app:"
echo ""
sudo docker logs geo7-app --tail 10 2>/dev/null || echo "   Container geo7-app não está rodando"
echo ""

# 4. Verificar se as portas estão abertas
echo "4. Verificando portas:"
echo ""
echo "   Porta 80 (HTTP):"
sudo netstat -tlnp | grep :80 || echo "   Porta 80 não está em uso"
echo ""
echo "   Porta 443 (HTTPS):"
sudo netstat -tlnp | grep :443 || echo "   Porta 443 não está em uso"
echo ""
echo "   Porta 8080 (Backend):"
sudo netstat -tlnp | grep :8080 || echo "   Porta 8080 não está em uso"
echo ""

# 5. Verificar nginx.conf
echo "5. Verificando nginx.conf:"
if [ -f "nginx.conf" ]; then
    echo "   ✅ nginx.conf existe"
    echo "   Testando configuração:"
    sudo docker exec geo7-web nginx -t 2>&1 || echo "   ⚠️ Erro na configuração do nginx"
else
    echo "   ❌ nginx.conf não encontrado no diretório atual"
fi
echo ""

# 6. Tentar reiniciar containers
echo "6. Tentando reiniciar containers..."
echo ""
sudo docker-compose up -d
echo ""

# 7. Aguardar e verificar novamente
echo "7. Aguardando 10 segundos e verificando novamente..."
sleep 10
echo ""
sudo docker ps
echo ""

# 8. Testar conectividade local
echo "8. Testando conectividade local:"
echo ""
echo "   HTTP (porta 80):"
curl -s -o /dev/null -w "%{http_code}" http://localhost/ || echo "   Erro ao conectar"
echo ""
echo "   Backend (porta 8080):"
curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/api/healthz || echo "   Erro ao conectar"
echo ""

echo "=== RESUMO ==="
echo ""
if sudo docker ps | grep -q "geo7-web"; then
    echo "✅ Container geo7-web está rodando"
else
    echo "❌ Container geo7-web NÃO está rodando"
    echo "   Execute: sudo docker-compose up -d"
fi

if sudo docker ps | grep -q "geo7-app"; then
    echo "✅ Container geo7-app está rodando"
else
    echo "❌ Container geo7-app NÃO está rodando"
    echo "   Execute: sudo docker-compose up -d"
fi
echo ""

