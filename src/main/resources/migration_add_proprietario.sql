-- Script de migração para adicionar campos do proprietário na tabela espacos
-- Execute este script se você já tem dados na tabela espacos

-- Adicionar colunas do proprietário
ALTER TABLE espacos 
ADD COLUMN IF NOT EXISTS nome_proprietario VARCHAR(100),
ADD COLUMN IF NOT EXISTS cnpj_proprietario VARCHAR(18);

-- Comentários para documentação
COMMENT ON COLUMN espacos.nome_proprietario IS 'Nome do proprietário do espaço';
COMMENT ON COLUMN espacos.cnpj_proprietario IS 'CNPJ do proprietário do espaço';
