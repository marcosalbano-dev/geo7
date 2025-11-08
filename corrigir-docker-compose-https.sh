#!/bin/bash

echo "=== CORRIGINDO DOCKER-COMPOSE PARA HTTPS ==="
echo "Data: $(date)"
echo ""

# Verificar se docker-compose.yml existe
if [ ! -f "docker-compose.yml" ]; then
    echo "❌ docker-compose.yml não encontrado no diretório atual"
    echo "Execute este script no diretório onde está o docker-compose.yml"
    exit 1
fi

# Fazer backup
echo "1. Fazendo backup do docker-compose.yml atual..."
cp docker-compose.yml docker-compose.yml.backup.$(date +%Y%m%d_%H%M%S)
echo "✅ Backup criado"

# Verificar se já tem volume SSL
if grep -q "/etc/ssl/geo7" docker-compose.yml; then
    echo "✅ Volume SSL já está configurado"
else
    echo "⚠️ Volume SSL não encontrado, será adicionado"
fi

# Verificar se já tem porta 443
if grep -q "443:443" docker-compose.yml; then
    echo "✅ Porta 443 já está configurada"
else
    echo "⚠️ Porta 443 não encontrada, será adicionada"
fi

echo ""
echo "2. Verificando estrutura do docker-compose.yml..."
echo "   (O script tentará adicionar as configurações necessárias)"
echo ""

# Criar script Python para modificar o docker-compose.yml
cat > /tmp/fix_docker_compose.py << 'PYTHON_SCRIPT'
import yaml
import sys
import os

# Ler docker-compose.yml
with open('docker-compose.yml', 'r') as f:
    compose = yaml.safe_load(f)

if 'services' not in compose:
    print("❌ Formato inválido do docker-compose.yml")
    sys.exit(1)

# Encontrar serviço web/nginx
web_service = None
web_service_name = None

for service_name in ['web', 'nginx', 'geo7-web']:
    if service_name in compose['services']:
        web_service = compose['services'][service_name]
        web_service_name = service_name
        break

if not web_service:
    print("❌ Serviço web/nginx não encontrado no docker-compose.yml")
    sys.exit(1)

print(f"✅ Serviço encontrado: {web_service_name}")

# Adicionar porta 443 se não existir
if 'ports' not in web_service:
    web_service['ports'] = []

ports_str = str(web_service['ports'])
if '443:443' not in ports_str:
    if '80:80' in ports_str or '"80:80"' in ports_str:
        # Adicionar 443 junto com 80
        if isinstance(web_service['ports'], list):
            if '"80:80"' in str(web_service['ports']) or '80:80' in str(web_service['ports']):
                web_service['ports'].append('443:443')
            else:
                web_service['ports'] = ['80:80', '443:443']
        print("✅ Porta 443 adicionada")
    else:
        web_service['ports'] = ['80:80', '443:443']
        print("✅ Portas 80 e 443 adicionadas")
else:
    print("✅ Porta 443 já existe")

# Adicionar volume SSL se não existir
if 'volumes' not in web_service:
    web_service['volumes'] = []

volumes_str = str(web_service['volumes'])
if '/etc/ssl/geo7' not in volumes_str:
    if isinstance(web_service['volumes'], list):
        web_service['volumes'].append('/etc/ssl/geo7:/etc/ssl/geo7:ro')
    else:
        web_service['volumes'] = [web_service['volumes'], '/etc/ssl/geo7:/etc/ssl/geo7:ro']
    print("✅ Volume SSL adicionado")
else:
    print("✅ Volume SSL já existe")

# Salvar
with open('docker-compose.yml', 'w') as f:
    yaml.dump(compose, f, default_flow_style=False, sort_keys=False)

print("✅ docker-compose.yml atualizado com sucesso!")
PYTHON_SCRIPT

# Tentar usar Python se disponível
if command -v python3 &> /dev/null; then
    python3 /tmp/fix_docker_compose.py
elif command -v python &> /dev/null; then
    python /tmp/fix_docker_compose.py
else
    echo "⚠️ Python não encontrado, usando método manual..."
    echo ""
    echo "Você precisa adicionar manualmente ao serviço 'web' ou 'nginx':"
    echo ""
    echo "  ports:"
    echo "    - \"80:80\""
    echo "    - \"443:443\""
    echo ""
    echo "  volumes:"
    echo "    - /etc/ssl/geo7:/etc/ssl/geo7:ro"
    echo ""
    exit 1
fi

echo ""
echo "3. Verificando configuração atualizada..."
if grep -q "443:443" docker-compose.yml && grep -q "/etc/ssl/geo7" docker-compose.yml; then
    echo "✅ Configuração está correta!"
    echo ""
    echo "Resumo das mudanças:"
    echo "  - Porta 443: ✅"
    echo "  - Volume SSL: ✅"
else
    echo "⚠️ Alguma configuração ainda está faltando"
    echo "Verifique manualmente o docker-compose.yml"
fi

echo ""
echo "4. Próximos passos:"
echo "   - Reconstruir o container web com o novo nginx.conf:"
echo "     sudo docker-compose build web"
echo "   - Reiniciar os containers:"
echo "     sudo docker-compose up -d"
echo "   - Verificar se HTTPS está funcionando:"
echo "     curl -k https://18-228-94-6.sslip.io"

