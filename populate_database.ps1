# Script para popular o banco de dados com dados de exemplo
# Execute este script no servidor EC2

Write-Host "=== Populando banco de dados com dados de exemplo ===" -ForegroundColor Green

# 1. Verificar se há dados nas tabelas
Write-Host "`n1. Verificando dados existentes..." -ForegroundColor Yellow
ssh ubuntu@18-228-94-6.sslip.io "docker exec geo7-geo7-postgres-1 psql -U geo7_app -d geo7_new -c 'SELECT COUNT(*) as municipios_count FROM ibge.municipios;'"
ssh ubuntu@18-228-94-6.sslip.io "docker exec geo7-geo7-postgres-1 psql -U geo7_app -d geo7_new -c 'SELECT COUNT(*) as lotes_count FROM geo7.lotes;'"

# 2. Inserir dados de exemplo em ibge.municipios se estiver vazio
Write-Host "`n2. Inserindo dados de exemplo em ibge.municipios..." -ForegroundColor Yellow
$municipiosSQL = @"
INSERT INTO ibge.municipios (id, nome, uf, regiao, mesoregiao, microregiao, latitude, longitude, area_modulo_fiscal) VALUES
(1, 'São Paulo', 'SP', 'Sudeste', 'Metropolitana de São Paulo', 'São Paulo', -23.5505, -46.6333, 1.0),
(2, 'Rio de Janeiro', 'RJ', 'Sudeste', 'Metropolitana do Rio de Janeiro', 'Rio de Janeiro', -22.9068, -43.1729, 1.0),
(3, 'Belo Horizonte', 'MG', 'Sudeste', 'Metropolitana de Belo Horizonte', 'Belo Horizonte', -19.9167, -43.9345, 1.0)
ON CONFLICT (id) DO NOTHING;
"@

ssh ubuntu@18-228-94-6.sslip.io "docker exec geo7-geo7-postgres-1 psql -U geo7_app -d geo7_new -c `"$municipiosSQL`""

# 3. Inserir dados de exemplo em geo7.situacoes_juridicas se estiver vazio
Write-Host "`n3. Inserindo dados de exemplo em geo7.situacoes_juridicas..." -ForegroundColor Yellow
$situacoesSQL = @"
INSERT INTO geo7.situacoes_juridicas (id, nome, ativo, dhc, dhm) VALUES
(1, 'Propriedade', true, NOW(), NOW()),
(2, 'Posse', true, NOW(), NOW()),
(3, 'Concessão', true, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
"@

ssh ubuntu@18-228-94-6.sslip.io "docker exec geo7-geo7-postgres-1 psql -U geo7_app -d geo7_new -c `"$situacoesSQL`""

# 4. Inserir dados de exemplo em geo7.usos se estiver vazio
Write-Host "`n4. Inserindo dados de exemplo em geo7.usos..." -ForegroundColor Yellow
$usosSQL = @"
INSERT INTO geo7.usos (id, nome, ativo, dhc, dhm) VALUES
(1, 'Residencial', true, NOW(), NOW()),
(2, 'Comercial', true, NOW(), NOW()),
(3, 'Industrial', true, NOW(), NOW()),
(4, 'Rural', true, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
"@

ssh ubuntu@18-228-94-6.sslip.io "docker exec geo7-geo7-postgres-1 psql -U geo7_app -d geo7_new -c `"$usosSQL`""

# 5. Inserir dados de exemplo em geo7.lotes
Write-Host "`n5. Inserindo dados de exemplo em geo7.lotes..." -ForegroundColor Yellow
$lotesSQL = @"
INSERT INTO geo7.lotes (id, numero, area, perimetro, municipio_id, situacao_juridica_id, denominacao_imovel, proprietario, dhc, dhm) VALUES
(1, '001', 1000.50, 200.25, 1, 1, 'Lote Residencial 001', 'João Silva', NOW(), NOW()),
(2, '002', 1500.75, 250.30, 2, 2, 'Lote Comercial 002', 'Maria Santos', NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
"@

ssh ubuntu@18-228-94-6.sslip.io "docker exec geo7-geo7-postgres-1 psql -U geo7_app -d geo7_new -c `"$lotesSQL`""

# 6. Verificar se os dados foram inseridos
Write-Host "`n6. Verificando dados inseridos..." -ForegroundColor Yellow
ssh ubuntu@18-228-94-6.sslip.io "docker exec geo7-geo7-postgres-1 psql -U geo7_app -d geo7_new -c 'SELECT COUNT(*) as municipios_count FROM ibge.municipios;'"
ssh ubuntu@18-228-94-6.sslip.io "docker exec geo7-geo7-postgres-1 psql -U geo7_app -d geo7_new -c 'SELECT COUNT(*) as lotes_count FROM geo7.lotes;'"

# 7. Testar os endpoints da API
Write-Host "`n7. Testando endpoints da API..." -ForegroundColor Yellow

# Fazer login para obter token
$loginResponse = ssh ubuntu@18-228-94-6.sslip.io "curl -s -X POST http://18-228-94-6.sslip.io:8080/auth/login -H 'Content-Type: application/json' -d '{\"email\":\"admin@geo7.com\",\"password\":\"admin123\"}'"
$token = ($loginResponse | ConvertFrom-Json).token

Write-Host "Token obtido: $($token.Substring(0, 20))..." -ForegroundColor Cyan

# Testar endpoint de municípios
Write-Host "`nTestando /api/municipios..." -ForegroundColor Cyan
ssh ubuntu@18-228-94-6.sslip.io "curl -s -X GET http://18-228-94-6.sslip.io:8080/api/municipios -H 'Authorization: Bearer $token' | jq ."

# Testar endpoint de lotes
Write-Host "`nTestando /api/lotes..." -ForegroundColor Cyan
ssh ubuntu@18-228-94-6.sslip.io "curl -s -X GET http://18-228-94-6.sslip.io:8080/api/lotes -H 'Authorization: Bearer $token' | jq ."

Write-Host "`n=== Script concluído ===" -ForegroundColor Green
Write-Host "Agora teste a aplicação Angular para ver se os dados estão carregando." -ForegroundColor Yellow
