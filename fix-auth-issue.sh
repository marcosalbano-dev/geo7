#!/bin/bash

echo "=== CORRIGINDO PROBLEMA DE AUTENTICAÇÃO ==="
echo "Data: $(date)"

# 1. Verificar se o container está rodando
echo ""
echo "1. Verificando containers..."
sudo docker ps

# 2. Verificar se há usuários no banco
echo ""
echo "2. Verificando usuários no banco..."
sudo docker exec geo7-postgres psql -U postgres -d geo7 -c "SELECT id, email, active FROM users;"

# 3. Criar usuário padrão se não existir
echo ""
echo "3. Criando usuário padrão..."
sudo docker exec geo7-postgres psql -U postgres -d geo7 -c "
INSERT INTO users (id, name, email, password, role, active) 
VALUES (
    '123e4567-e89b-12d3-a456-426614174000', 
    'Administrador', 
    'admin@geo7.com', 
    '\$2a\$10\$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 
    'ADMIN', 
    true
) ON CONFLICT (email) DO UPDATE SET 
    password = '\$2a\$10\$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi',
    active = true;"

# 4. Verificar se o usuário foi criado
echo ""
echo "4. Verificando usuário criado..."
sudo docker exec geo7-postgres psql -U postgres -d geo7 -c "SELECT id, email, active FROM users WHERE email = 'admin@geo7.com';"

# 5. Testar o endpoint de login
echo ""
echo "5. Testando endpoint de login..."
curl -X POST \
  -H 'Content-Type: application/json' \
  -d '{"email":"admin@geo7.com","password":"admin123"}' \
  -v \
  http://localhost:8080/api/auth/login

echo ""
echo "=== CORREÇÃO CONCLUÍDA ==="
echo ""
echo "Usuário padrão criado:"
echo "  Email: admin@geo7.com"
echo "  Senha: admin123"
echo ""
echo "Teste o login no frontend agora!"
