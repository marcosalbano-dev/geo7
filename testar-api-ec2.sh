#!/bin/bash

# Script para testar API na EC2
echo "=== TESTANDO API ==="
echo ""

# 1. Testar backend diretamente
echo "1. Testando backend diretamente (porta 8080):"
curl -s http://localhost:8080/api/healthz
echo ""
echo ""

# 2. Testar via proxy nginx
echo "2. Testando via proxy nginx (/api/healthz):"
curl -s http://localhost/api/healthz
echo ""
echo ""

# 3. Verificar configuração do nginx
echo "3. Verificando configuração do nginx:"
sudo docker exec geo7-web cat /etc/nginx/conf.d/default.conf | grep -A 5 "location /api/"
echo ""

# 4. Verificar se o container app está acessível
echo "4. Verificando conectividade entre containers:"
sudo docker exec geo7-web ping -c 2 app 2>/dev/null || echo "   ⚠️ Não foi possível fazer ping para 'app'"
echo ""

# 5. Verificar logs do nginx para erros
echo "5. Últimos logs do nginx (erros):"
sudo docker logs geo7-web --tail 20 2>&1 | grep -i "error\|warn\|fail" | tail -5
echo ""

# 6. Testar requisição completa
echo "6. Testando requisição completa:"
echo "   curl -v http://localhost/api/auth/login"
curl -v -X POST http://localhost/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@test.com","password":"test"}' 2>&1 | head -20

