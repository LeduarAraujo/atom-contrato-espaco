package com.atom.contratoespaco.service;

import com.atom.contratoespaco.dto.RelatorioDTO;
import com.atom.contratoespaco.dto.TipoContratoDTO;
import com.atom.contratoespaco.dto.EspacoDTO;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class PdfService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public byte[] gerarDocumento(RelatorioDTO relatorio, TipoContratoDTO tipoContrato, EspacoDTO espaco) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        
        try {
            System.out.println("Iniciando geração de PDF profissional...");
            System.out.println("Relatório: " + relatorio.getNomeCliente());
            System.out.println("Tipo de contrato: " + tipoContrato.getTipo());
            
            // Configurar documento com margens maiores para aparência profissional
            Document document = new Document(PageSize.A4, 60, 60, 80, 60);
            PdfWriter writer = PdfWriter.getInstance(document, outputStream);
            
            // Adicionar evento para numeração de páginas
            writer.setPageEvent(new PageNumberEvent());
            
            // Definir metadados do documento
            String nomeArquivo = gerarNomeArquivo(relatorio, tipoContrato);
            document.addTitle(nomeArquivo);
            document.addSubject("Contrato de Espaço - " + relatorio.getNomeCliente());
            document.addKeywords("contrato, espaço, " + tipoContrato.getTipo());
            document.addCreator("Sistema de Contratos de Espaço");
            document.addAuthor("Sistema de Contratos");
            
            document.open();
            
            // Adicionar cabeçalho simplificado
            adicionarCabecalhoSimplificado(document, espaco);
            
            // Adicionar título principal simplificado
            adicionarTituloSimplificado(document, tipoContrato);
            
            // Adicionar conteúdo central do contrato
            adicionarConteudoCentral(document, tipoContrato, relatorio);
            
            // Adicionar seção de assinaturas baseada no tipo de contrato
            if (tipoContrato.getTipo().toString().equals("RECIBO")) {
                System.out.println("MONTANDO RECIBO");
                adicionarAssinaturaRecibo(document, relatorio, espaco);
            } else {
                System.out.println("MONTANDO CONTRATO");
                adicionarAssinaturasProfissionais(document, relatorio, espaco);
            }
            
            document.close();
            System.out.println("PDF profissional gerado com sucesso!");
            return outputStream.toByteArray();
            
        } catch (Exception e) {
            System.err.println("Erro ao gerar PDF: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erro ao gerar PDF", e);
        }
    }

    private String processarTemplate(String template, RelatorioDTO relatorio) {
        // Formatar CPF/CNPJ com máscara
        String cpfFormatado = formatarCpfCnpj(relatorio.getCpfCliente());
        
        // Formatar data com dia da semana
        String dataComDiaSemana = formatarDataComDiaSemana(relatorio.getDataFesta());
        
        // Formatar valor por extenso
        String valorExtenso = formatarValorPorExtenso(relatorio.getValorPago().doubleValue());
        
        return template
                .replace("{nome}", relatorio.getNomeCliente())
                .replace("{cpf}", cpfFormatado)
                .replace("{valor}", String.format("R$ %.2f", relatorio.getValorPago()))
                .replace("{valorExtenso}", valorExtenso)
                .replace("{dataFesta}", dataComDiaSemana)
                .replace("{horaInicio}", relatorio.getHoraInicio().toString())
                .replace("{horaFim}", relatorio.getHoraFim().toString())
                .replace("{valorIntegral}", relatorio.getValorIntegral() ? "Integral" : "Parcial");
    }
    
    private String formatarCpfCnpj(String cpfCnpj) {
        if (cpfCnpj == null || cpfCnpj.trim().isEmpty()) {
            return "";
        }
        
        // Remove todos os caracteres não numéricos
        String numeros = cpfCnpj.replaceAll("\\D", "");
        
        // Aplica máscara baseada no tamanho
        if (numeros.length() == 11) {
            // CPF: 000.000.000-00
            return numeros.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
        } else if (numeros.length() == 14) {
            // CNPJ: 00.000.000/0000-00
            return numeros.replaceAll("(\\d{2})(\\d{3})(\\d{3})(\\d{4})(\\d{2})", "$1.$2.$3/$4-$5");
        } else {
            // Retorna o valor original se não for CPF nem CNPJ válido
            return cpfCnpj;
        }
    }
    
    private String formatarDataComDiaSemana(LocalDate data) {
        if (data == null) {
            return "";
        }
        
        String dataFormatada = data.format(DATE_FORMATTER);
        String diaSemana = data.getDayOfWeek().getDisplayName(TextStyle.FULL, new Locale("pt", "BR"));
        
        // Capitaliza a primeira letra
        diaSemana = diaSemana.substring(0, 1).toUpperCase() + diaSemana.substring(1);
        
        return String.format("%s (%s)", dataFormatada, diaSemana);
    }
    
    private String formatarValorPorExtenso(double valor) {
        try {
            String[] unidades = {"", "um", "dois", "três", "quatro", "cinco", "seis", "sete", "oito", "nove"};
            String[] dezenas = {"", "", "vinte", "trinta", "quarenta", "cinquenta", "sessenta", "setenta", "oitenta", "noventa"};
            String[] dezenasEspeciais = {"dez", "onze", "doze", "treze", "quatorze", "quinze", "dezesseis", "dezessete", "dezoito", "dezenove"};
            String[] centenas = {"", "cento", "duzentos", "trezentos", "quatrocentos", "quinhentos", "seiscentos", "setecentos", "oitocentos", "novecentos"};

            if (valor == 0) return "zero reais";
            if (valor < 0) return "valor inválido";

            long inteiro = (long) Math.floor(valor);
            long decimal = Math.round((valor - inteiro) * 100);

            StringBuilder resultado = new StringBuilder();

            // Centenas
            if (inteiro >= 100) {
                long centena = inteiro / 100;
                if (centena == 1 && inteiro == 100) {
                    resultado.append("cem");
                } else {
                    resultado.append(centenas[(int) centena]);
                }
                inteiro = inteiro % 100;
                if (inteiro > 0) resultado.append(" e ");
            }

            // Dezenas
            if (inteiro >= 20) {
                long dezena = inteiro / 10;
                resultado.append(dezenas[(int) dezena]);
                inteiro = inteiro % 10;
                if (inteiro > 0) resultado.append(" e ");
            } else if (inteiro >= 10) {
                resultado.append(dezenasEspeciais[(int) (inteiro - 10)]);
                inteiro = 0;
            }

            // Unidades
            if (inteiro > 0) {
                resultado.append(unidades[(int) inteiro]);
            }

            // Plural
            if (inteiro != 1) {
                resultado.append(" reais");
            } else {
                resultado.append(" real");
            }

            // Centavos
            if (decimal > 0) {
                resultado.append(" e ");
                if (decimal >= 20) {
                    long dezenaCent = decimal / 10;
                    resultado.append(dezenas[(int) dezenaCent]);
                    decimal = decimal % 10;
                    if (decimal > 0) resultado.append(" e ");
                } else if (decimal >= 10) {
                    resultado.append(dezenasEspeciais[(int) (decimal - 10)]);
                    decimal = 0;
                }
                if (decimal > 0) {
                    resultado.append(unidades[(int) decimal]);
                }
                if (decimal != 1) {
                    resultado.append(" centavos");
                } else {
                    resultado.append(" centavo");
                }
            }

            return resultado.toString();
        } catch (Exception e) {
            System.err.println("Erro ao formatar valor por extenso: " + e.getMessage());
            e.printStackTrace();
            return "erro na formatação";
        }
    }

    public void salvarPdf(byte[] pdfBytes, String nomeArquivo) throws IOException {
        String diretorio = "pdfs/";
        
        // Criar diretório se não existir
        Files.createDirectories(Paths.get(diretorio));
        
        // Salvar arquivo
        Files.write(Paths.get(diretorio + nomeArquivo), pdfBytes);
    }

    public byte[] carregarPdf(String nomeArquivo) throws IOException {
        return Files.readAllBytes(Paths.get("pdfs/" + nomeArquivo));
    }
    
    private String gerarNomeArquivo(RelatorioDTO relatorio, TipoContratoDTO tipoContrato) {
        // Gerar nome do arquivo baseado no cliente e data
        String nomeCliente = relatorio.getNomeCliente().replaceAll("[^a-zA-Z0-9]", "_");
        String dataFesta = relatorio.getDataFesta().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String tipoContratoLimpo = tipoContrato.getTipo().toString().replaceAll("[^a-zA-Z0-9]", "_");
        
        return String.format("%s_%s_%s.pdf", nomeCliente, dataFesta, tipoContratoLimpo);
    }
    
    // Classe interna para numeração de páginas
    private static class PageNumberEvent extends PdfPageEventHelper {
        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte cb = writer.getDirectContent();
            Phrase footer = new Phrase(String.format("Página %d", writer.getPageNumber()), 
                FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.GRAY));
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, footer,
                (document.right() - document.left()) / 2 + document.leftMargin(),
                document.bottom() - 10, 0);
        }
    }

    private void adicionarCabecalhoSimplificado(Document document, EspacoDTO espaco) throws DocumentException {
        // Criar tabela para cabeçalho
        PdfPTable cabecalho = new PdfPTable(2);
        cabecalho.setWidthPercentage(100);
        cabecalho.setWidths(new float[]{3f, 1f});
        
        // Coluna esquerda - Apenas nome da empresa
        PdfPCell celulaInfo = new PdfPCell();
        celulaInfo.setBorder(Rectangle.NO_BORDER);
        celulaInfo.setPadding(10);
        
        Font fontEmpresa = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.DARK_GRAY);
        
        if (espaco != null) {
            Paragraph nomeEmpresa = new Paragraph(espaco.getNome(), fontEmpresa);
            celulaInfo.addElement(nomeEmpresa);
        }
        
        // Coluna direita - Logo
        PdfPCell celulaLogo = new PdfPCell();
        celulaLogo.setBorder(Rectangle.NO_BORDER);
        celulaLogo.setHorizontalAlignment(Element.ALIGN_RIGHT);
        celulaLogo.setVerticalAlignment(Element.ALIGN_TOP);
        celulaLogo.setPadding(10);
        
        if (espaco != null && espaco.getLogoData() != null && espaco.getLogoMimeType() != null) {
            try {
                Image logo = Image.getInstance(espaco.getLogoData());
                logo.scaleToFit(80, 80);
                celulaLogo.addElement(logo);
            } catch (Exception e) {
                System.err.println("Erro ao adicionar logo: " + e.getMessage());
            }
        }
        
        cabecalho.addCell(celulaInfo);
        cabecalho.addCell(celulaLogo);
        
        document.add(cabecalho);
        
        // Linha separadora
        document.add(new Paragraph(" "));
        PdfPTable linhaSeparadora = new PdfPTable(1);
        linhaSeparadora.setWidthPercentage(100);
        PdfPCell linha = new PdfPCell();
        linha.setBorder(Rectangle.BOTTOM);
        linha.setBorderColor(BaseColor.LIGHT_GRAY);
        linha.setBorderWidth(1);
        linha.setPadding(5);
        linha.addElement(new Paragraph(" "));
        linhaSeparadora.addCell(linha);
        document.add(linhaSeparadora);
        document.add(new Paragraph(" "));
    }

    private void adicionarTituloSimplificado(Document document, TipoContratoDTO tipoContrato) throws DocumentException {
        if (tipoContrato.getTitulo() != null && !tipoContrato.getTitulo().trim().isEmpty()) {
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, BaseColor.BLACK);
            Paragraph title = new Paragraph(tipoContrato.getTitulo().toUpperCase(), titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);
        }
    }


    private void adicionarConteudoCentral(Document document, TipoContratoDTO tipoContrato, RelatorioDTO relatorio) throws DocumentException {
        // Processar o template substituindo os placeholders
        String textoProcessado = processarTemplate(tipoContrato.getTextoTemplate(), relatorio);
        
        // Adicionar o texto processado ao PDF com formatação profissional
        String[] linhas = textoProcessado.split("\n");
        
        Font contentFont = FontFactory.getFont(FontFactory.HELVETICA, 11, BaseColor.BLACK);
        
        for (String linha : linhas) {
            if (!linha.trim().isEmpty()) {
                Paragraph paragraph = new Paragraph(linha, contentFont);
                paragraph.setSpacingAfter(6);
                paragraph.setLeading(14);
                paragraph.setFirstLineIndent(20); // Indentação da primeira linha
                document.add(paragraph);
            } else {
                Paragraph emptyLine = new Paragraph(" ");
                emptyLine.setSpacingAfter(6);
                document.add(emptyLine);
            }
        }
    }

    private void adicionarAssinaturasProfissionais(Document document, RelatorioDTO relatorio, EspacoDTO espaco) throws DocumentException {
        // Espaçamento antes das assinaturas
        document.add(new Paragraph(" "));
        document.add(new Paragraph(" "));
        
        // Título da seção
        Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.DARK_GRAY);
        Paragraph sectionTitle = new Paragraph("ASSINATURAS", sectionFont);
        sectionTitle.setSpacingAfter(15);
        document.add(sectionTitle);
        
        // Tabela para assinaturas
        PdfPTable tabelaAssinaturas = new PdfPTable(2);
        tabelaAssinaturas.setWidthPercentage(100);
        tabelaAssinaturas.setWidths(new float[]{1f, 1f});
        
        // Cliente
        PdfPCell celulaCliente = new PdfPCell();
        celulaCliente.setBorder(Rectangle.BOX);
        celulaCliente.setBorderColor(BaseColor.LIGHT_GRAY);
        celulaCliente.setPadding(15);
        celulaCliente.setHorizontalAlignment(Element.ALIGN_CENTER);
        
        Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.DARK_GRAY);
        Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 11, BaseColor.BLACK);
        
        Paragraph clienteLabel = new Paragraph("LOCATÁRIO", labelFont);
        clienteLabel.setAlignment(Element.ALIGN_CENTER);
        celulaCliente.addElement(clienteLabel);
        
        celulaCliente.addElement(new Paragraph(" "));
        celulaCliente.addElement(new Paragraph(" "));
        
        // Linha para assinatura
        PdfPTable linhaAssinatura = new PdfPTable(1);
        linhaAssinatura.setWidthPercentage(100);
        PdfPCell linha = new PdfPCell();
        linha.setBorder(Rectangle.BOTTOM);
        linha.setBorderColor(BaseColor.BLACK);
        linha.setBorderWidth(1);
        linha.setPadding(5);
        linha.addElement(new Paragraph(" "));
        linhaAssinatura.addCell(linha);
        celulaCliente.addElement(linhaAssinatura);
        
        Paragraph clienteNome = new Paragraph(relatorio.getNomeCliente(), valueFont);
        clienteNome.setAlignment(Element.ALIGN_CENTER);
        celulaCliente.addElement(clienteNome);
        
        String cpfFormatado = formatarCpfCnpj(relatorio.getCpfCliente());
        Paragraph clienteCpf = new Paragraph("CPF: " + cpfFormatado, valueFont);
        clienteCpf.setAlignment(Element.ALIGN_CENTER);
        celulaCliente.addElement(clienteCpf);
        
        String dataHoje = LocalDate.now().format(DATE_FORMATTER);
        Paragraph dataCliente = new Paragraph("Data: " + dataHoje, valueFont);
        dataCliente.setAlignment(Element.ALIGN_CENTER);
        celulaCliente.addElement(dataCliente);
        
        // Proprietário
        PdfPCell celulaProprietario = new PdfPCell();
        celulaProprietario.setBorder(Rectangle.BOX);
        celulaProprietario.setBorderColor(BaseColor.LIGHT_GRAY);
        celulaProprietario.setPadding(15);
        celulaProprietario.setHorizontalAlignment(Element.ALIGN_CENTER);
        
        Paragraph proprietarioLabel = new Paragraph("LOCADOR", labelFont);
        proprietarioLabel.setAlignment(Element.ALIGN_CENTER);
        celulaProprietario.addElement(proprietarioLabel);
        
        celulaProprietario.addElement(new Paragraph(" "));
        celulaProprietario.addElement(new Paragraph(" "));
        
        // Linha para assinatura
        PdfPTable linhaAssinaturaProp = new PdfPTable(1);
        linhaAssinaturaProp.setWidthPercentage(100);
        PdfPCell linhaProp = new PdfPCell();
        linhaProp.setBorder(Rectangle.BOTTOM);
        linhaProp.setBorderColor(BaseColor.BLACK);
        linhaProp.setBorderWidth(1);
        linhaProp.setPadding(5);
        linhaProp.addElement(new Paragraph(" "));
        linhaAssinaturaProp.addCell(linhaProp);
        celulaProprietario.addElement(linhaAssinaturaProp);
        
        if (espaco != null && espaco.getNomeProprietario() != null && !espaco.getNomeProprietario().trim().isEmpty()) {
            Paragraph proprietarioNome = new Paragraph(espaco.getNomeProprietario(), valueFont);
            proprietarioNome.setAlignment(Element.ALIGN_CENTER);
            celulaProprietario.addElement(proprietarioNome);
            
            if (espaco.getCnpjProprietario() != null && !espaco.getCnpjProprietario().trim().isEmpty()) {
                String cnpjFormatado = formatarCpfCnpj(espaco.getCnpjProprietario());
                Paragraph proprietarioCnpj = new Paragraph("CNPJ: " + cnpjFormatado, valueFont);
                proprietarioCnpj.setAlignment(Element.ALIGN_CENTER);
                celulaProprietario.addElement(proprietarioCnpj);
            }
        } else {
            Paragraph naoInformado = new Paragraph("Não informado", valueFont);
            naoInformado.setAlignment(Element.ALIGN_CENTER);
            celulaProprietario.addElement(naoInformado);
        }
        
        String dataHojeProp = LocalDate.now().format(DATE_FORMATTER);
        Paragraph dataProprietario = new Paragraph("Data: " + dataHojeProp, valueFont);
        dataProprietario.setAlignment(Element.ALIGN_CENTER);
        celulaProprietario.addElement(dataProprietario);
        
        tabelaAssinaturas.addCell(celulaCliente);
        tabelaAssinaturas.addCell(celulaProprietario);
        
        document.add(tabelaAssinaturas);
    }

    private void adicionarAssinaturaRecibo(Document document, RelatorioDTO relatorio, EspacoDTO espaco) throws DocumentException {
        // Espaçamento antes das assinaturas
        document.add(new Paragraph(" "));
        document.add(new Paragraph(" "));
        
        // Título da seção
        Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.DARK_GRAY);
        Paragraph sectionTitle = new Paragraph("ASSINATURA", sectionFont);
        sectionTitle.setSpacingAfter(15);
        document.add(sectionTitle);
        
        // Container para assinatura do locador (sem bordas)
        PdfPTable containerAssinatura = new PdfPTable(1);
        containerAssinatura.setWidthPercentage(50);
        containerAssinatura.setHorizontalAlignment(Element.ALIGN_RIGHT);
        
        PdfPCell celulaAssinatura = new PdfPCell();
        celulaAssinatura.setBorder(Rectangle.NO_BORDER);
        celulaAssinatura.setPadding(15);
        celulaAssinatura.setHorizontalAlignment(Element.ALIGN_CENTER);
        
        Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.DARK_GRAY);
        Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 11, BaseColor.BLACK);
        
        Paragraph locadorLabel = new Paragraph("LOCADOR", labelFont);
        locadorLabel.setAlignment(Element.ALIGN_CENTER);
        celulaAssinatura.addElement(locadorLabel);
        
        celulaAssinatura.addElement(new Paragraph(" "));
        celulaAssinatura.addElement(new Paragraph(" "));
        
        // Linha para assinatura
        PdfPTable linhaAssinatura = new PdfPTable(1);
        linhaAssinatura.setWidthPercentage(100);
        PdfPCell linha = new PdfPCell();
        linha.setBorder(Rectangle.BOTTOM);
        linha.setBorderColor(BaseColor.BLACK);
        linha.setBorderWidth(1);
        linha.setPadding(5);
        linha.addElement(new Paragraph(" "));
        linhaAssinatura.addCell(linha);
        celulaAssinatura.addElement(linhaAssinatura);
        
        if (espaco != null && espaco.getNomeProprietario() != null && !espaco.getNomeProprietario().trim().isEmpty()) {
            Paragraph locadorNome = new Paragraph(espaco.getNomeProprietario(), valueFont);
            locadorNome.setAlignment(Element.ALIGN_CENTER);
            celulaAssinatura.addElement(locadorNome);
            
            if (espaco.getCnpjProprietario() != null && !espaco.getCnpjProprietario().trim().isEmpty()) {
                String cnpjFormatado = formatarCpfCnpj(espaco.getCnpjProprietario());
                Paragraph locadorCnpj = new Paragraph("CNPJ: " + cnpjFormatado, valueFont);
                locadorCnpj.setAlignment(Element.ALIGN_CENTER);
                celulaAssinatura.addElement(locadorCnpj);
            }
        } else {
            Paragraph naoInformado = new Paragraph("Não informado", valueFont);
            naoInformado.setAlignment(Element.ALIGN_CENTER);
            celulaAssinatura.addElement(naoInformado);
        }
        
        String dataHoje = LocalDate.now().format(DATE_FORMATTER);
        Paragraph dataLocador = new Paragraph("Data: " + dataHoje, valueFont);
        dataLocador.setAlignment(Element.ALIGN_CENTER);
        celulaAssinatura.addElement(dataLocador);
        
        containerAssinatura.addCell(celulaAssinatura);
        document.add(containerAssinatura);
    }

}