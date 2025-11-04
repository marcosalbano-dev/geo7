#!/bin/bash

echo "=== Testando endpoints da API ==="

# 1. Fazer login
echo "1. Fazendo login..."
LOGIN_RESPONSE=$(curl -s -X POST http://18-228-94-6.sslip.io:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@geo7.com","password":"admin123"}')

echo "Resposta do login: $LOGIN_RESPONSE"

TOKEN=$(echo $LOGIN_RESPONSE | jq -r '.token')
echo "Token: ${TOKEN:0:20}..."

# 2. Testar API diretamente (porta 8080)
echo -e "\n2. Testando API diretamente (porta 8080)..."

echo "Testando /api/municipios..."
curl -s -X GET http://18-228-94-6.sslip.io:8080/api/municipios \
  -H "Authorization: Bearer $TOKEN" | jq '.[0:3]' 2>/dev/null || echo "Erro ao processar JSON"

echo -e "\nTestando /api/lotes..."
curl -s -X GET http://18-228-94-6.sslip.io:8080/api/lotes \
  -H "Authorization: Bearer $TOKEN" | jq . 2>/dev/null || echo "Erro ao processar JSON"

# 3. Testar via Caddy (porta 80)
echo -e "\n3. Testando via Caddy (porta 80)..."

echo "Testando /api/municipios via Caddy..."
curl -s -X GET http://18-228-94-6.sslip.io/api/municipios \
  -H "Authorization: Bearer $TOKEN" \
  -H "Origin: http://18-228-94-6.sslip.io:8081" | jq '.[0:3]' 2>/dev/null || echo "Erro ao processar JSON"

echo -e "\nTestando /api/lotes via Caddy..."
curl -s -X GET http://18-228-94-6.sslip.io/api/lotes \
  -H "Authorization: Bearer $TOKEN" \
  -H "Origin: http://18-228-94-6.sslip.io:8081" | jq . 2>/dev/null || echo "Erro ao processar JSON"

# 4. Verificar logs
echo -e "\n4. Logs da API (últimas 5 linhas)..."
docker logs api --tail 5

echo -e "\n=== Teste concluído ==="
