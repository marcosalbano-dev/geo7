#!/bin/bash

echo "=== ATUALIZANDO DOCKER-COMPOSE PARA HTTPS ==="
echo ""

# Verificar se docker-compose.yml existe
if [ ! -f "docker-compose.yml" ]; then
    echo "❌ docker-compose.yml não encontrado"
    exit 1
fi

echo "Verificando configuração atual..."
echo ""

# Verificar porta 443
if grep -q "443:443" docker-compose.yml; then
    echo "✅ Porta 443 já configurada"
else
    echo "⚠️ Porta 443 NÃO encontrada"
    NEEDS_UPDATE=true
fi

# Verificar volume SSL
if grep -q "/etc/ssl/geo7" docker-compose.yml; then
    echo "✅ Volume SSL já configurado"
else
    echo "⚠️ Volume SSL NÃO encontrado"
    NEEDS_UPDATE=true
fi

if [ "$NEEDS_UPDATE" = true ]; then
    echo ""
    echo "📝 Você precisa adicionar manualmente ao serviço 'web' ou 'nginx' no docker-compose.yml:"
    echo ""
    echo "  ports:"
    echo "    - \"80:80\""
    echo "    - \"443:443\"    # ← Adicionar esta linha"
    echo ""
    echo "  volumes:"
    echo "    - /home/ubuntu/geo7-web:/usr/share/nginx/html"
    echo "    - ./nginx.conf:/etc/nginx/conf.d/default.conf"
    echo "    - /etc/ssl/geo7:/etc/ssl/geo7:ro    # ← Adicionar esta linha"
    echo ""
    echo "Depois execute:"
    echo "  sudo docker-compose down"
    echo "  sudo docker-compose up -d"
else
    echo ""
    echo "✅ Tudo configurado! Docker-compose está pronto para HTTPS"
fi

