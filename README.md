# Sistema de Contratos e Espaços - Backend

Sistema backend desenvolvido em Spring Boot 17 para gerenciamento de contratos de locação de espaços e recibos de pagamento.

## 🚀 Tecnologias Utilizadas

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **PostgreSQL**
- **Maven**
- **iText 7** (Geração de PDFs)
- **Lombok**

## 📋 Funcionalidades

### ✅ APIs Implementadas

#### Clientes
- `GET /api/clientes` - Listar todos os clientes
- `GET /api/clientes/{id}` - Buscar cliente por ID
- `POST /api/clientes` - Criar novo cliente
- `PUT /api/clientes/{id}` - Atualizar cliente
- `DELETE /api/clientes/{id}` - Excluir cliente
- `GET /api/clientes/buscar?nome={nome}` - Buscar clientes por nome

#### Espaços
- `GET /api/espacos` - Listar todos os espaços
- `GET /api/espacos/ativos` - Listar espaços ativos
- `GET /api/espacos/{id}` - Buscar espaço por ID
- `POST /api/espacos` - Criar novo espaço
- `PUT /api/espacos/{id}` - Atualizar espaço
- `DELETE /api/espacos/{id}` - Excluir espaço
- `GET /api/espacos/buscar?nome={nome}` - Buscar espaços por nome

#### Contratos de Locação
- `GET /api/contratos/locacao` - Listar todos os contratos
- `GET /api/contratos/locacao/{id}` - Buscar contrato por ID
- `POST /api/contratos/locacao` - Criar novo contrato
- `PUT /api/contratos/locacao/{id}` - Atualizar contrato
- `DELETE /api/contratos/locacao/{id}` - Excluir contrato
- `GET /api/contratos/locacao/{id}/pdf` - Gerar PDF do contrato
- `GET /api/contratos/locacao/cliente/{clienteId}` - Buscar contratos por cliente
- `GET /api/contratos/locacao/espaco/{espacoId}` - Buscar contratos por espaço

#### Recibos de Pagamento
- `GET /api/recibos` - Listar todos os recibos
- `GET /api/recibos/{id}` - Buscar recibo por ID
- `POST /api/recibos` - Criar novo recibo
- `PUT /api/recibos/{id}` - Atualizar recibo
- `DELETE /api/recibos/{id}` - Excluir recibo
- `GET /api/recibos/{id}/pdf` - Gerar PDF do recibo
- `GET /api/recibos/cliente/{clienteId}` - Buscar recibos por cliente

#### Relatórios
- `GET /api/relatorios/dashboard` - Dashboard com estatísticas
- `GET /api/relatorios/contratos/periodo` - Contratos por período
- `GET /api/relatorios/recibos/periodo` - Recibos por período

## 🗄️ Banco de Dados

### Configuração
- **Host**: ep-weathered-salad-a5vgz2iq-pooler.us-east-2.aws.neon.tech
- **Porta**: 5432
- **Banco**: neondb
- **Usuário**: neondb_owner

### Estrutura das Tabelas

#### clientes
- id (BIGSERIAL PRIMARY KEY)
- nome (VARCHAR(100) NOT NULL)
- cpf (VARCHAR(14) UNIQUE NOT NULL)
- endereco (VARCHAR(200))
- telefone (VARCHAR(20))
- email (VARCHAR(100))
- created_at, updated_at (TIMESTAMP)

#### espacos
- id (BIGSERIAL PRIMARY KEY)
- nome (VARCHAR(100) NOT NULL)
- descricao (VARCHAR(500))
- capacidade (INTEGER)
- valor_hora (DECIMAL(10,2))
- valor_diario (DECIMAL(10,2))
- ativo (BOOLEAN DEFAULT TRUE)
- created_at, updated_at (TIMESTAMP)

#### contratos_locacao
- id (BIGSERIAL PRIMARY KEY)
- cliente_id (BIGINT NOT NULL) - FK para clientes
- espaco_id (BIGINT NOT NULL) - FK para espacos
- data_inicio (DATE NOT NULL)
- data_fim (DATE NOT NULL)
- valor_total (DECIMAL(10,2) NOT NULL)
- valor_entrada (DECIMAL(10,2))
- texto_personalizado (TEXT)
- clausulas (TEXT)
- status (VARCHAR(20) DEFAULT 'ATIVO')
- created_at, updated_at (TIMESTAMP)

#### recibos
- id (BIGSERIAL PRIMARY KEY)
- cliente_id (BIGINT NOT NULL) - FK para clientes
- data_pagamento (DATE NOT NULL)
- valor (DECIMAL(10,2) NOT NULL)
- forma_pagamento (VARCHAR(50))
- descricao_servico (VARCHAR(500))
- texto_personalizado (TEXT)
- observacoes (TEXT)
- created_at, updated_at (TIMESTAMP)

## 🚀 Como Executar

### Pré-requisitos
- Java 17+
- Maven 3.6+
- PostgreSQL (ou acesso ao banco configurado)

### Passos

1. **Clone o repositório**
```bash
git clone <repository-url>
cd atom-contrato-espaco
```

2. **Configure o banco de dados**
As configurações já estão no `application.yml`, mas você pode alterar se necessário.

3. **Execute o projeto**
```bash
mvn spring-boot:run
```

4. **Acesse a aplicação**
- API disponível em: `http://localhost:8080/api`
- Health check: `http://localhost:8080/api/actuator/health`

## 📝 Exemplos de Uso

### Criar um Cliente
```bash
curl -X POST http://localhost:8080/api/clientes \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "João Silva",
    "cpf": "123.456.789-01",
    "endereco": "Rua das Flores, 123",
    "telefone": "(11) 99999-9999",
    "email": "joao.silva@email.com"
  }'
```

### Criar um Contrato de Locação
```bash
curl -X POST http://localhost:8080/api/contratos/locacao \
  -H "Content-Type: application/json" \
  -d '{
    "clienteId": 1,
    "espacoId": 1,
    "dataInicio": "2024-02-01",
    "dataFim": "2024-02-28",
    "valorTotal": 3000.00,
    "valorEntrada": 500.00,
    "textoPersonalizado": "Contrato especial para treinamento",
    "clausulas": "1. O locatário se compromete a manter o espaço em perfeitas condições."
  }'
```

### Gerar PDF do Contrato
```bash
curl -X GET http://localhost:8080/api/contratos/locacao/1/pdf \
  --output contrato.pdf
```

## 🔧 Configurações

### CORS
O projeto está configurado para aceitar requisições de qualquer origem (`*`) para desenvolvimento. Em produção, configure adequadamente.

### Logs
- Logs de SQL habilitados para desenvolvimento
- Nível DEBUG para pacotes da aplicação

### Validação
- Validação automática de DTOs com Bean Validation
- Mensagens de erro personalizadas

## 📊 Dados de Exemplo

O projeto inclui dados de exemplo que são carregados automaticamente:
- 5 clientes
- 6 espaços (1 inativo)
- 5 contratos de locação
- 8 recibos de pagamento

## 🛠️ Desenvolvimento

### Estrutura do Projeto
```
src/main/java/com/atom/contratoespaco/
├── config/          # Configurações (CORS, etc.)
├── controller/      # Controllers REST
├── dto/            # Data Transfer Objects
├── entity/         # Entidades JPA
├── repository/     # Repositórios JPA
└── service/        # Serviços de negócio
```

### Adicionando Novos Endpoints
1. Crie o DTO se necessário
2. Implemente o service
3. Crie o controller
4. Adicione validações
5. Teste a API

## 📄 Licença

Este projeto está sob a licença MIT.
