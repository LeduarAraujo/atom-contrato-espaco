-- Script de migração para simplificar a tabela espacos
-- Remove colunas desnecessárias, mantendo apenas nome e logo_url

-- Verificar se as colunas existem antes de removê-las
DO $$ 
BEGIN
    -- Remover coluna descricao se existir
    IF EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name = 'espacos' 
        AND column_name = 'descricao'
    ) THEN
        ALTER TABLE espacos DROP COLUMN descricao;
        RAISE NOTICE 'Coluna descricao removida da tabela espacos';
    END IF;

    -- Remover coluna capacidade se existir
    IF EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name = 'espacos' 
        AND column_name = 'capacidade'
    ) THEN
        ALTER TABLE espacos DROP COLUMN capacidade;
        RAISE NOTICE 'Coluna capacidade removida da tabela espacos';
    END IF;

    -- Remover coluna valor_hora se existir
    IF EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name = 'espacos' 
        AND column_name = 'valor_hora'
    ) THEN
        ALTER TABLE espacos DROP COLUMN valor_hora;
        RAISE NOTICE 'Coluna valor_hora removida da tabela espacos';
    END IF;

    -- Remover coluna valor_diario se existir
    IF EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name = 'espacos' 
        AND column_name = 'valor_diario'
    ) THEN
        ALTER TABLE espacos DROP COLUMN valor_diario;
        RAISE NOTICE 'Coluna valor_diario removida da tabela espacos';
    END IF;

    -- Remover coluna ativo se existir
    IF EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name = 'espacos' 
        AND column_name = 'ativo'
    ) THEN
        ALTER TABLE espacos DROP COLUMN ativo;
        RAISE NOTICE 'Coluna ativo removida da tabela espacos';
    END IF;

    -- Adicionar coluna logo_url se não existir
    IF NOT EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name = 'espacos' 
        AND column_name = 'logo_url'
    ) THEN
        ALTER TABLE espacos ADD COLUMN logo_url VARCHAR(500);
        RAISE NOTICE 'Coluna logo_url adicionada à tabela espacos';
    END IF;

    RAISE NOTICE 'Migração concluída - tabela espacos simplificada';
END $$;
