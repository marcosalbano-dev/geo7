-- Criar schemas necessários
CREATE SCHEMA IF NOT EXISTS geo7;
CREATE SCHEMA IF NOT EXISTS ibge;

-- Definir search_path para incluir todos os schemas
ALTER DATABASE geo7 SET search_path = geo7, public, ibge;

-- Conceder permissões
GRANT USAGE ON SCHEMA geo7 TO postgres;
GRANT USAGE ON SCHEMA ibge TO postgres;
GRANT CREATE ON SCHEMA geo7 TO postgres;
GRANT CREATE ON SCHEMA ibge TO postgres;
