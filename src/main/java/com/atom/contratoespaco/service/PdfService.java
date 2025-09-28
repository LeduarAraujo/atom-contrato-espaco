package com.atom.contratoespaco.service;

import com.atom.contratoespaco.dto.RelatorioDTO;
import com.atom.contratoespaco.dto.TipoContratoDTO;
import com.atom.contratoespaco.dto.EspacoDTO;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
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
            System.out.println("Iniciando geração de PDF...");
            System.out.println("Relatório: " + relatorio.getNomeCliente());
            System.out.println("Tipo de contrato: " + tipoContrato.getTipo());
            
            Document document = new Document(PageSize.A4, 50, 50, 50, 50);
            PdfWriter.getInstance(document, outputStream);
            
            // Definir metadados do documento, incluindo o nome do arquivo
            String nomeArquivo = gerarNomeArquivo(relatorio, tipoContrato);
            document.addTitle(nomeArquivo);
            document.addSubject("Contrato de Espaço - " + relatorio.getNomeCliente());
            document.addKeywords("contrato, espaço, " + tipoContrato.getTipo());
            document.addCreator("Sistema de Contratos de Espaço");
            document.addAuthor("Sistema de Contratos");
            
            document.open();
            
            // Adicionar logo no canto superior direito (se existir)
            if (espaco != null && espaco.getLogoData() != null && espaco.getLogoMimeType() != null) {
                try {
                    Image logo = Image.getInstance(espaco.getLogoData());
                    logo.scaleToFit(80, 80);
                    logo.setAbsolutePosition(document.getPageSize().getWidth() - 100, document.getPageSize().getHeight() - 100);
                    document.add(logo);
                } catch (Exception e) {
                    System.err.println("Erro ao adicionar logo: " + e.getMessage());
                }
            }
            
                    // Adicionar título centralizado
                    if (tipoContrato.getTitulo() != null && !tipoContrato.getTitulo().trim().isEmpty()) {
                        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
                        Paragraph title = new Paragraph(tipoContrato.getTitulo(), titleFont);
                        title.setAlignment(Element.ALIGN_CENTER);
                        title.setSpacingAfter(40); // Aumentar espaçamento após o título
                        document.add(title);
                    }
            
            // Processar o template substituindo os placeholders
            String textoProcessado = processarTemplate(tipoContrato.getTextoTemplate(), relatorio);
            
            // Adicionar o texto processado ao PDF
            String[] linhas = textoProcessado.split("\n");
            
            for (String linha : linhas) {
                if (!linha.trim().isEmpty()) {
                    Paragraph paragraph = new Paragraph(linha);
                    paragraph.setSpacingAfter(8); // Espaçamento entre parágrafos
                    paragraph.setLeading(16); // Espaçamento entre linhas
                    document.add(paragraph);
                } else {
                    Paragraph emptyLine = new Paragraph(" ");
                    emptyLine.setSpacingAfter(8);
                    document.add(emptyLine); // Linha em branco
                }
            }
            
            document.close();
            System.out.println("PDF gerado com sucesso!");
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
}