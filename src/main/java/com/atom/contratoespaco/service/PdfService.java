package com.atom.contratoespaco.service;

import com.atom.contratoespaco.dto.RelatorioDTO;
import com.atom.contratoespaco.dto.TipoContratoDTO;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class PdfService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public byte[] gerarDocumento(RelatorioDTO relatorio, TipoContratoDTO tipoContrato) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        
        try {
            System.out.println("Iniciando geração de PDF...");
            System.out.println("Relatório: " + relatorio.getNomeCliente());
            System.out.println("Tipo de contrato: " + tipoContrato.getTipo());
            
            Document document = new Document();
            PdfWriter.getInstance(document, outputStream);
            document.open();
            
            // Processar o template substituindo os placeholders
            String textoProcessado = processarTemplate(tipoContrato.getTextoTemplate(), relatorio);
            
            // Adicionar o texto processado ao PDF
            String[] linhas = textoProcessado.split("\n");
            
            for (String linha : linhas) {
                if (!linha.trim().isEmpty()) {
                    Paragraph paragraph = new Paragraph(linha);
                    document.add(paragraph);
                } else {
                    document.add(new Paragraph(" ")); // Linha em branco
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
        return template
                .replace("{nome}", relatorio.getNomeCliente())
                .replace("{cpf}", relatorio.getCpfCliente())
                .replace("{valor}", String.format("R$ %.2f", relatorio.getValorPago()))
                .replace("{dataFesta}", relatorio.getDataFesta().format(DATE_FORMATTER))
                .replace("{horaInicio}", relatorio.getHoraInicio().toString())
                .replace("{horaFim}", relatorio.getHoraFim().toString())
                .replace("{valorIntegral}", relatorio.getValorIntegral() ? "Integral" : "Parcial");
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
}