# Instruções de Execução - Sistema de Contratos

## 🚀 Como Executar o Projeto

### Pré-requisitos
- Java 17 ou superior
- Maven 3.6 ou superior
- PostgreSQL (ou acesso ao banco configurado)

### 1. Configuração do Banco de Dados

O projeto já está configurado para usar o banco PostgreSQL fornecido:
- **Host**: ep-weathered-salad-a5vgz2iq-pooler.us-east-2.aws.neon.tech
- **Porta**: 5432
- **Banco**: neondb
- **Usuário**: neondb_owner
- **Senha**: npg_8wKSN7HUXuOF

### 2. Executar a Aplicação

```bash
# Navegar para o diretório do projeto
cd /home/leduar/Documentos/workspace/ws_back/atom-contrato-espaco

# Compilar o projeto
mvn clean compile

# Executar a aplicação
mvn spring-boot:run
```

### 3. Verificar se a Aplicação Está Funcionando

A aplicação estará disponível em:
- **API Base**: http://localhost:8080/api
- **Health Check**: http://localhost:8080/api/actuator/health

### 4. Testar as APIs

#### Opção 1: Usar o script de teste automático
```bash
# Executar o script de teste (requer jq instalado)
./test-api.sh
```

#### Opção 2: Testes manuais

**Listar clientes:**
```bash
curl -X GET http://localhost:8080/api/clientes
```

**Criar um cliente:**
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

**Listar espaços:**
```bash
curl -X GET http://localhost:8080/api/espacos
```

**Criar um contrato:**
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
    "textoPersonalizado": "Contrato especial",
    "clausulas": "1. Cláusula de teste"
  }'
```

**Gerar PDF do contrato:**
```bash
curl -X GET http://localhost:8080/api/contratos/locacao/1/pdf \
  --output contrato.pdf
```

### 5. Dados de Exemplo

O sistema já vem com dados de exemplo pré-carregados:
- 5 clientes
- 6 espaços (1 inativo)
- 5 contratos de locação
- 8 recibos de pagamento

### 6. Estrutura das APIs

#### Endpoints Principais:

**Clientes:**
- GET /api/clientes - Listar todos
- GET /api/clientes/{id} - Buscar por ID
- POST /api/clientes - Criar novo
- PUT /api/clientes/{id} - Atualizar
- DELETE /api/clientes/{id} - Excluir
- GET /api/clientes/buscar?nome={nome} - Buscar por nome

**Espaços:**
- GET /api/espacos - Listar todos
- GET /api/espacos/ativos - Listar ativos
- GET /api/espacos/{id} - Buscar por ID
- POST /api/espacos - Criar novo
- PUT /api/espacos/{id} - Atualizar
- DELETE /api/espacos/{id} - Excluir

**Contratos de Locação:**
- GET /api/contratos/locacao - Listar todos
- GET /api/contratos/locacao/{id} - Buscar por ID
- POST /api/contratos/locacao - Criar novo
- PUT /api/contratos/locacao/{id} - Atualizar
- DELETE /api/contratos/locacao/{id} - Excluir
- GET /api/contratos/locacao/{id}/pdf - Gerar PDF

**Recibos:**
- GET /api/recibos - Listar todos
- GET /api/recibos/{id} - Buscar por ID
- POST /api/recibos - Criar novo
- PUT /api/recibos/{id} - Atualizar
- DELETE /api/recibos/{id} - Excluir
- GET /api/recibos/{id}/pdf - Gerar PDF

**Relatórios:**
- GET /api/relatorios/dashboard - Dashboard

### 7. Logs e Debug

A aplicação está configurada para mostrar logs detalhados:
- Logs SQL habilitados
- Nível DEBUG para pacotes da aplicação
- Logs no console durante a execução

### 8. Solução de Problemas

**Erro de conexão com banco:**
- Verificar se as credenciais estão corretas
- Verificar conectividade de rede

**Erro de compilação:**
- Verificar se Java 17 está instalado
- Executar `mvn clean compile`

**Erro ao gerar PDF:**
- Verificar se o contrato/recibo existe
- Verificar logs da aplicação

### 9. Desenvolvimento

Para desenvolvimento, você pode:
- Modificar as entidades em `src/main/java/com/atom/contratoespaco/entity/`
- Adicionar novos endpoints nos controllers
- Customizar a geração de PDF no `PdfService`
- Adicionar validações nos DTOs

### 10. Build para Produção

```bash
# Gerar JAR executável
mvn clean package

# Executar o JAR
java -jar target/contrato-espaco-1.0.0.jar
```
