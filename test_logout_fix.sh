#!/bin/bash

echo "=== Testando problema após logout ==="

# 1. Testar login
echo "1. Testando login..."
LOGIN_RESPONSE=$(curl -s -X POST http://18-228-94-6.sslip.io:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@geo7.com","password":"admin123"}')

TOKEN=$(echo $LOGIN_RESPONSE | jq -r '.token')
echo "Token: ${TOKEN:0:20}..."

# 2. Testar endpoints
echo "2. Testando endpoints..."
echo "Testando /municipios..."
curl -s -X GET http://18-228-94-6.sslip.io/municipios \
  -H "Authorization: Bearer $TOKEN" \
  -H "Origin: http://18-228-94-6.sslip.io:8081" | jq '.[0:2]'

echo "Testando /lotes..."
curl -s -X GET http://18-228-94-6.sslip.io/lotes \
  -H "Authorization: Bearer $TOKEN" \
  -H "Origin: http://18-228-94-6.sslip.io:8081" | jq .

# 3. Testar logout
echo "3. Testando logout..."
LOGOUT_RESPONSE=$(curl -s -X POST http://18-228-94-6.sslip.io:8080/auth/logout \
  -H "Authorization: Bearer $TOKEN")
echo "Resposta do logout: $LOGOUT_RESPONSE"

# 4. Testar login novamente após logout
echo "4. Testando login após logout..."
LOGIN_RESPONSE2=$(curl -s -X POST http://18-228-94-6.sslip.io:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@geo7.com","password":"admin123"}')

TOKEN2=$(echo $LOGIN_RESPONSE2 | jq -r '.token')
echo "Novo token: ${TOKEN2:0:20}..."

# 5. Testar endpoints com novo token
echo "5. Testando endpoints com novo token..."
echo "Testando /municipios..."
curl -s -X GET http://18-228-94-6.sslip.io/municipios \
  -H "Authorization: Bearer $TOKEN2" \
  -H "Origin: http://18-228-94-6.sslip.io:8081" | jq '.[0:2]'

echo "Testando /lotes..."
curl -s -X GET http://18-228-94-6.sslip.io/lotes \
  -H "Authorization: Bearer $TOKEN2" \
  -H "Origin: http://18-228-94-6.sslip.io:8081" | jq .

echo "=== Teste concluído ==="
