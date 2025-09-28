-- Script de criação das tabelas do banco de dados - Versão Simplificada

-- Limpar todas as tabelas existentes
DROP TABLE IF EXISTS recibos CASCADE;
DROP TABLE IF EXISTS contratos_locacao CASCADE;
DROP TABLE IF EXISTS clientes CASCADE;
DROP TABLE IF EXISTS tipos_contrato CASCADE;
DROP TABLE IF EXISTS espacos CASCADE;
DROP TABLE IF EXISTS relatorios CASCADE;

-- Tabela de espaços
CREATE TABLE espacos (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    logo_url VARCHAR(500),
    logo_data BYTEA,
    logo_mime_type VARCHAR(100),
    nome_proprietario VARCHAR(100),
    cnpj_proprietario VARCHAR(18),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabela de tipos de contrato (Contrato ou Recibo)
CREATE TABLE tipos_contrato (
    id BIGSERIAL PRIMARY KEY,
    espaco_id BIGINT NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    titulo VARCHAR(200) NOT NULL,
    descricao VARCHAR(100),
    texto_template TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (espaco_id) REFERENCES espacos(id)
);

-- Tabela de relatórios gerados
CREATE TABLE relatorios (
    id BIGSERIAL PRIMARY KEY,
    tipo_contrato_id BIGINT NOT NULL,
    nome_cliente VARCHAR(100) NOT NULL,
    cpf_cliente VARCHAR(14) NOT NULL,
    data_festa DATE NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fim TIME NOT NULL,
    valor_pago DECIMAL(10,2) NOT NULL,
    valor_integral BOOLEAN DEFAULT FALSE,
    arquivo_gerado VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (tipo_contrato_id) REFERENCES tipos_contrato(id)
);

-- Índices para melhorar performance
CREATE INDEX idx_espacos_nome ON espacos(nome);
CREATE INDEX idx_tipos_contrato_espaco ON tipos_contrato(espaco_id);
CREATE INDEX idx_tipos_contrato_tipo ON tipos_contrato(tipo);
CREATE INDEX idx_relatorios_tipo_contrato ON relatorios(tipo_contrato_id);
CREATE INDEX idx_relatorios_cliente ON relatorios(nome_cliente);
CREATE INDEX idx_relatorios_data ON relatorios(data_festa);