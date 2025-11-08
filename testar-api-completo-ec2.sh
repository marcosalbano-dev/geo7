#!/bin/bash

# Teste completo da API
echo "=== TESTE COMPLETO DA API ==="
echo ""

# 1. Testar backend diretamente
echo "1. Backend direto (porta 8080):"
curl -v http://localhost:8080/api/healthz 2>&1 | grep -E "(< HTTP|status)"
echo ""

# 2. Testar via proxy
echo "2. Via proxy nginx (/api/healthz):"
curl -v http://localhost/api/healthz 2>&1 | grep -E "(< HTTP|status)"
echo ""

# 3. Testar POST (simulação de login)
echo "3. Testando POST /api/auth/login:"
curl -v -X POST http://localhost/api/auth/login \
  -H "Content-Type: application/json" \
  -H "Origin: http://18-228-94-6.sslip.io" \
  -d '{"email":"test@test.com","password":"test"}' 2>&1 | head -30
echo ""

# 4. Verificar logs do nginx em tempo real
echo "4. Monitorando logs do nginx (aguarde 5 segundos e tente fazer login no navegador):"
echo "   (Pressione Ctrl+C para parar)"
timeout 10 sudo docker logs -f geo7-web 2>&1 | grep -E "(GET|POST|OPTIONS|error)" || echo "   (Timeout após 10 segundos)"

