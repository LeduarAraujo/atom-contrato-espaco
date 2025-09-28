#!/bin/bash

# Script para testar as APIs do sistema de contratos

BASE_URL="http://localhost:8080/api"

echo "=== Testando APIs do Sistema de Contratos ==="
echo

# Teste 1: Listar clientes
echo "1. Listando clientes..."
curl -s -X GET "$BASE_URL/clientes" | jq .
echo

# Teste 2: Criar um novo cliente
echo "2. Criando um novo cliente..."
CLIENTE_RESPONSE=$(curl -s -X POST "$BASE_URL/clientes" \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Teste API",
    "cpf": "111.222.333-44",
    "endereco": "Rua Teste, 123",
    "telefone": "(11) 99999-8888",
    "email": "teste@email.com"
  }')

echo "$CLIENTE_RESPONSE" | jq .

# Extrair ID do cliente criado
CLIENTE_ID=$(echo "$CLIENTE_RESPONSE" | jq -r '.id')
echo "Cliente criado com ID: $CLIENTE_ID"
echo

# Teste 3: Listar espaços
echo "3. Listando espaços..."
curl -s -X GET "$BASE_URL/espacos" | jq .
echo

# Teste 4: Criar um contrato de locação
echo "4. Criando um contrato de locação..."
CONTRATO_RESPONSE=$(curl -s -X POST "$BASE_URL/contratos/locacao" \
  -H "Content-Type: application/json" \
  -d '{
    "clienteId": '$CLIENTE_ID',
    "espacoId": 1,
    "dataInicio": "2024-03-01",
    "dataFim": "2024-03-31",
    "valorTotal": 2500.00,
    "valorEntrada": 500.00,
    "textoPersonalizado": "Contrato de teste criado via API",
    "clausulas": "1. Este é um contrato de teste.\n2. Criado automaticamente via API."
  }')

echo "$CONTRATO_RESPONSE" | jq .

# Extrair ID do contrato criado
CONTRATO_ID=$(echo "$CONTRATO_RESPONSE" | jq -r '.id')
echo "Contrato criado com ID: $CONTRATO_ID"
echo

# Teste 5: Criar um recibo
echo "5. Criando um recibo..."
RECIBO_RESPONSE=$(curl -s -X POST "$BASE_URL/recibos" \
  -H "Content-Type: application/json" \
  -d '{
    "clienteId": '$CLIENTE_ID',
    "dataPagamento": "2024-02-15",
    "valor": 500.00,
    "formaPagamento": "PIX",
    "descricaoServico": "Entrada do contrato de teste",
    "textoPersonalizado": "Recebemos a quantia de R$ 500,00",
    "observacoes": "Pagamento referente à entrada do contrato de teste"
  }')

echo "$RECIBO_RESPONSE" | jq .

# Extrair ID do recibo criado
RECIBO_ID=$(echo "$RECIBO_RESPONSE" | jq -r '.id')
echo "Recibo criado com ID: $RECIBO_ID"
echo

# Teste 6: Gerar PDF do contrato
echo "6. Gerando PDF do contrato..."
curl -s -X GET "$BASE_URL/contratos/locacao/$CONTRATO_ID/pdf" \
  --output "contrato-teste-$CONTRATO_ID.pdf"
echo "PDF do contrato salvo como: contrato-teste-$CONTRATO_ID.pdf"
echo

# Teste 7: Gerar PDF do recibo
echo "7. Gerando PDF do recibo..."
curl -s -X GET "$BASE_URL/recibos/$RECIBO_ID/pdf" \
  --output "recibo-teste-$RECIBO_ID.pdf"
echo "PDF do recibo salvo como: recibo-teste-$RECIBO_ID.pdf"
echo

# Teste 8: Dashboard
echo "8. Consultando dashboard..."
curl -s -X GET "$BASE_URL/relatorios/dashboard" | jq .
echo

echo "=== Testes concluídos ==="
