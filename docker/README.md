# Docker Setup - Geo7

Este diretório contém os arquivos necessários para executar a aplicação Geo7 usando Docker.

## Arquivos Incluídos

- `Dockerfile`: Configuração para build da aplicação Spring Boot
- `docker-compose.yml`: Orquestração dos serviços (PostgreSQL + Aplicação)
- `env.example`: Exemplo de variáveis de ambiente
- `README.md`: Este arquivo com instruções

## Pré-requisitos

- Docker instalado
- Docker Compose instalado

## Como Usar

### 1. Preparar o Ambiente

Copie o arquivo de exemplo de variáveis de ambiente:
```bash
cp docker/env.example docker/.env
```

Edite o arquivo `.env` se necessário para ajustar as configurações.

### 2. Executar a Aplicação

Navegue até o diretório docker e execute:
```bash
cd docker
docker-compose up -d
```

### 3. Verificar os Serviços

Para verificar se os serviços estão rodando:
```bash
docker-compose ps
```

### 4. Ver Logs

Para ver os logs da aplicação:
```bash
docker-compose logs -f app
```

Para ver os logs do banco de dados:
```bash
docker-compose logs -f postgres
```

### 5. Parar os Serviços

Para parar todos os serviços:
```bash
docker-compose down
```

Para parar e remover volumes (CUIDADO: isso apagará os dados do banco):
```bash
docker-compose down -v
```

## Acessos

- **Aplicação**: http://localhost:8080
- **Banco PostgreSQL**: localhost:5432
  - Usuário: postgres
  - Senha: Axp@01fal12
  - Database: geo7

## Comandos Úteis

### Rebuild da Aplicação
```bash
docker-compose build app
docker-compose up -d app
```

### Acessar o Container da Aplicação
```bash
docker-compose exec app bash
```

### Acessar o Banco de Dados
```bash
docker-compose exec postgres psql -U postgres -d geo7
```

### Limpar Tudo (CUIDADO)
```bash
docker-compose down -v
docker system prune -a
```

## Estrutura dos Serviços

- **postgres**: Banco de dados PostgreSQL 15
- **app**: Aplicação Spring Boot Geo7

## Volumes

- `postgres_data`: Dados persistentes do PostgreSQL

## Redes

- `geo7-network`: Rede interna para comunicação entre os serviços
