-- Script de migração para adicionar coluna logo_url na tabela espacos
-- Execute este script se a tabela espacos já existir sem a coluna logo_url

-- Verificar se a coluna logo_url não existe e adicioná-la
DO $$ 
BEGIN
    IF NOT EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name = 'espacos' 
        AND column_name = 'logo_url'
    ) THEN
        ALTER TABLE espacos ADD COLUMN logo_url VARCHAR(500);
        RAISE NOTICE 'Coluna logo_url adicionada à tabela espacos';
    ELSE
        RAISE NOTICE 'Coluna logo_url já existe na tabela espacos';
    END IF;
END $$;

