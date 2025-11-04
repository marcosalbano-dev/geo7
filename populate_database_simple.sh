#!/bin/bash

echo "=== Populando banco de dados com dados de exemplo ==="

# 1. Verificar dados existentes
echo "1. Verificando dados existentes..."
docker exec geo7-geo7-postgres-1 psql -U geo7_app -d geo7_new -c "SELECT COUNT(*) as municipios_count FROM ibge.municipios;"
docker exec geo7-geo7-postgres-1 psql -U geo7_app -d geo7_new -c "SELECT COUNT(*) as lotes_count FROM geo7.lotes;"

# 2. Inserir municípios de exemplo
echo "2. Inserindo municípios de exemplo..."
docker exec geo7-geo7-postgres-1 psql -U geo7_app -d geo7_new -c "
INSERT INTO ibge.municipios (id, nome, uf, regiao, mesoregiao, microregiao, latitude, longitude, area_modulo_fiscal) VALUES
(1, 'São Paulo', 'SP', 'Sudeste', 'Metropolitana de São Paulo', 'São Paulo', -23.5505, -46.6333, 1.0),
(2, 'Rio de Janeiro', 'RJ', 'Sudeste', 'Metropolitana do Rio de Janeiro', 'Rio de Janeiro', -22.9068, -43.1729, 1.0),
(3, 'Belo Horizonte', 'MG', 'Sudeste', 'Metropolitana de Belo Horizonte', 'Belo Horizonte', -19.9167, -43.9345, 1.0)
ON CONFLICT (id) DO NOTHING;
"

# 3. Inserir situações jurídicas de exemplo
echo "3. Inserindo situações jurídicas de exemplo..."
docker exec geo7-geo7-postgres-1 psql -U geo7_app -d geo7_new -c "
INSERT INTO geo7.situacoes_juridicas (id, nome, ativo, dhc, dhm) VALUES
(1, 'Propriedade', true, NOW(), NOW()),
(2, 'Posse', true, NOW(), NOW()),
(3, 'Concessão', true, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
"

# 4. Inserir usos de exemplo
echo "4. Inserindo usos de exemplo..."
docker exec geo7-geo7-postgres-1 psql -U geo7_app -d geo7_new -c "
INSERT INTO geo7.usos (id, nome, ativo, dhc, dhm) VALUES
(1, 'Residencial', true, NOW(), NOW()),
(2, 'Comercial', true, NOW(), NOW()),
(3, 'Industrial', true, NOW(), NOW()),
(4, 'Rural', true, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
"

# 5. Inserir lotes de exemplo
echo "5. Inserindo lotes de exemplo..."
docker exec geo7-geo7-postgres-1 psql -U geo7_app -d geo7_new -c "
INSERT INTO geo7.lotes (id, numero, area, perimetro, municipio_id, situacao_juridica_id, denominacao_imovel, proprietario, dhc, dhm) VALUES
(1, '001', 1000.50, 200.25, 1, 1, 'Lote Residencial 001', 'João Silva', NOW(), NOW()),
(2, '002', 1500.75, 250.30, 2, 2, 'Lote Comercial 002', 'Maria Santos', NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
"

# 6. Verificar dados inseridos
echo "6. Verificando dados inseridos..."
docker exec geo7-geo7-postgres-1 psql -U geo7_app -d geo7_new -c "SELECT COUNT(*) as municipios_count FROM ibge.municipios;"
docker exec geo7-geo7-postgres-1 psql -U geo7_app -d geo7_new -c "SELECT COUNT(*) as lotes_count FROM geo7.lotes;"

# 7. Testar API
echo "7. Testando API..."

# Fazer login
echo "Fazendo login..."
LOGIN_RESPONSE=$(curl -s -X POST http://18-228-94-6.sslip.io:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@geo7.com","password":"admin123"}')

TOKEN=$(echo $LOGIN_RESPONSE | jq -r '.token')
echo "Token obtido: ${TOKEN:0:20}..."

# Testar municípios
echo "Testando /api/municipios..."
curl -s -X GET http://18-228-94-6.sslip.io:8080/api/municipios \
  -H "Authorization: Bearer $TOKEN" | jq .

# Testar lotes
echo "Testando /api/lotes..."
curl -s -X GET http://18-228-94-6.sslip.io:8080/api/lotes \
  -H "Authorization: Bearer $TOKEN" | jq .

echo "=== Script concluído ==="
echo "Agora teste a aplicação Angular para ver se os dados estão carregando."
