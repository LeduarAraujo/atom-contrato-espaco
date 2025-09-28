-- Dados iniciais para o banco de dados

-- Inserir espaços de exemplo
INSERT INTO espacos (nome, logo_url) VALUES 
('Salão de Festas Jardim das Flores', 'https://example.com/logo-salao-jardim.png'),
('Quadra Esportiva Central', 'https://example.com/logo-quadra-central.png'),
('Centro de Eventos Premium', 'https://example.com/logo-centro-premium.png');

-- Inserir tipos de contrato de exemplo
INSERT INTO tipos_contrato (espaco_id, tipo, texto_template) VALUES 
(1, 'CONTRATO', 'CONTRATO DE LOCAÇÃO DE ESPAÇO

LOCADOR: {nome} - CPF: {cpf}
LOCATÁRIO: Empresa XYZ

Pelo presente instrumento particular, as partes acima qualificadas têm entre si justo e acordado o seguinte:

1. OBJETO: O LOCADOR cede ao LOCATÁRIO o espaço localizado no endereço Rua das Flores, 123, para realização de evento no dia {dataFesta}, das {horaInicio} às {horaFim}.

2. VALOR: O valor total da locação é de {valor}, sendo o pagamento integral confirmado.

3. OBRIGAÇÕES: O LOCATÁRIO se compromete a utilizar o espaço de forma adequada e devolvê-lo nas mesmas condições.

LOCADOR: _________________     LOCATÁRIO: _________________
Data: ___/___/___             Data: ___/___/___'),

(1, 'RECIBO', 'RECIBO DE PAGAMENTO

Recebi de {nome}, portador do CPF {cpf}, a quantia de {valor} referente ao pagamento integral da locação do espaço para evento no dia {dataFesta}, das {horaInicio} às {horaFim}.

Para maior clareza, firmo o presente recibo.

Data: ___/___/___

Assinatura: _________________
Nome: Empresa XYZ
CPF: 00.000.000/0001-00'),

(2, 'CONTRATO', 'CONTRATO DE LOCAÇÃO DE QUADRA ESPORTIVA

LOCADOR: {nome} - CPF: {cpf}
LOCATÁRIO: Associação Esportiva ABC

As partes acima qualificadas acordam:

1. OBJETO: Locação da Quadra Esportiva Central para atividade esportiva no dia {dataFesta}, das {horaInicio} às {horaFim}.

2. VALOR: {valor} - Pagamento: {valorIntegral}

3. RESPONSABILIDADES: O LOCATÁRIO é responsável pela conservação e devolução do espaço.

LOCADOR: _________________     LOCATÁRIO: _________________
Data: ___/___/___             Data: ___/___/___'),

(3, 'RECIBO', 'RECIBO DE LOCAÇÃO

Recebi de {nome} (CPF: {cpf}) o valor de {valor} como pagamento pela locação do Centro de Eventos Premium para o dia {dataFesta}, das {horaInicio} às {horaFim}.

Este recibo comprova o pagamento integral do serviço contratado.

Data: ___/___/___

_________________
Centro de Eventos Premium
CNPJ: 11.111.111/0001-11');