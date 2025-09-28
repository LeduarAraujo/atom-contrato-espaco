package com.atom.contratoespaco.controller;

import com.atom.contratoespaco.dto.ReservaDTO;
import com.atom.contratoespaco.dto.TipoContratoDTO;
import com.atom.contratoespaco.dto.EspacoDTO;
import com.atom.contratoespaco.service.PdfService;
import com.atom.contratoespaco.service.ReservaService;
import com.atom.contratoespaco.service.TipoContratoService;
import com.atom.contratoespaco.service.EspacoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@RestController
@RequestMapping("/pdf")
public class PdfController {

    @Autowired
    private PdfService pdfService;

    @Autowired
    private ReservaService reservaService;

    @Autowired
    private TipoContratoService tipoContratoService;

    @Autowired
    private EspacoService espacoService;

    @GetMapping("/reserva/{reservaId}")
    public ResponseEntity<byte[]> gerarPdfReserva(@PathVariable Long reservaId, @RequestParam Long tipoContratoId) {
        try {
            System.out.println("=== DEBUG: Gerando PDF para reserva " + reservaId + " ===");
            
            // Buscar a reserva
            Optional<ReservaDTO> reservaOpt = reservaService.buscarReservaPorId(reservaId);
            if (!reservaOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            ReservaDTO reserva = reservaOpt.get();
            
            // Buscar o tipo de contrato
            Optional<TipoContratoDTO> tipoContratoOpt = tipoContratoService.buscarTipoContratoPorId(tipoContratoId);
            if (!tipoContratoOpt.isPresent()) {
                return ResponseEntity.badRequest().build();
            }
            
            TipoContratoDTO tipoContrato = tipoContratoOpt.get();
            
            // Buscar o espaço
            Optional<EspacoDTO> espacoOpt = espacoService.buscarEspacoPorId(reserva.getEspacoId());
            if (!espacoOpt.isPresent()) {
                return ResponseEntity.badRequest().build();
            }
            
            EspacoDTO espaco = espacoOpt.get();
            
            // Gerar PDF
            byte[] pdfBytes = pdfService.gerarDocumento(reserva, tipoContrato, espaco);
            
            // Nome do arquivo
            String nomeArquivo = gerarNomeArquivo(reserva, tipoContrato);
            
            // Headers para download
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", nomeArquivo);
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
            
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
            
        } catch (Exception e) {
            System.err.println("=== ERRO ao gerar PDF ===");
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private String gerarNomeArquivo(ReservaDTO reserva, TipoContratoDTO tipoContrato) {
        String nomeCliente = reserva.getNomeCliente().replaceAll("[^a-zA-Z0-9]", "_");
        String dataFesta = reserva.getDataFesta().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return String.format("%s_%s_%s.pdf", 
                tipoContrato.getTipo().toString().toLowerCase(), 
                nomeCliente, 
                dataFesta);
    }
}
