-- Script para corrigir o banco de dados removendo colunas desnecessárias

-- Remover colunas que não existem mais no schema atual
ALTER TABLE espacos DROP COLUMN IF EXISTS ativo;
ALTER TABLE espacos DROP COLUMN IF EXISTS descricao;
ALTER TABLE espacos DROP COLUMN IF EXISTS capacidade;
ALTER TABLE espacos DROP COLUMN IF EXISTS valor_hora;
ALTER TABLE espacos DROP COLUMN IF EXISTS valor_diario;

-- Verificar se a coluna logo_url existe, se não, criar
ALTER TABLE espacos ADD COLUMN IF NOT EXISTS logo_url VARCHAR(500);

-- Verificar se as colunas created_at e updated_at existem
ALTER TABLE espacos ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE espacos ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- Recriar índices corretos
DROP INDEX IF EXISTS idx_espacos_ativo;
CREATE INDEX IF NOT EXISTS idx_espacos_nome ON espacos(nome);

