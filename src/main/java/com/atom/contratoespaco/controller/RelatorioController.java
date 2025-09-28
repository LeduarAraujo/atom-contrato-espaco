package com.atom.contratoespaco.controller;

import com.atom.contratoespaco.dto.RelatorioDTO;
import com.atom.contratoespaco.dto.TipoContratoDTO;
import com.atom.contratoespaco.service.RelatorioService;
import com.atom.contratoespaco.service.TipoContratoService;
import com.atom.contratoespaco.service.PdfService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/relatorios")
@RequiredArgsConstructor
public class RelatorioController {

    private final RelatorioService relatorioService;
    private final TipoContratoService tipoContratoService;
    private final PdfService pdfService;

    @GetMapping
    public ResponseEntity<List<RelatorioDTO>> listarRelatorios() {
        List<RelatorioDTO> relatorios = relatorioService.listarRelatorios();
        return ResponseEntity.ok(relatorios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RelatorioDTO> buscarRelatorioPorId(@PathVariable Long id) {
        return relatorioService.buscarRelatorioPorId(id)
                .map(relatorio -> ResponseEntity.ok(relatorio))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/tipo-contrato/{tipoContratoId}")
    public ResponseEntity<List<RelatorioDTO>> buscarRelatoriosPorTipoContrato(@PathVariable Long tipoContratoId) {
        List<RelatorioDTO> relatorios = relatorioService.buscarRelatoriosPorTipoContrato(tipoContratoId);
        return ResponseEntity.ok(relatorios);
    }

    @GetMapping("/periodo")
    public ResponseEntity<List<RelatorioDTO>> buscarRelatoriosPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        List<RelatorioDTO> relatorios = relatorioService.buscarRelatoriosPorPeriodo(dataInicio, dataFim);
        return ResponseEntity.ok(relatorios);
    }

    @GetMapping("/cliente")
    public ResponseEntity<List<RelatorioDTO>> buscarRelatoriosPorNomeCliente(@RequestParam String nome) {
        List<RelatorioDTO> relatorios = relatorioService.buscarRelatoriosPorNomeCliente(nome);
        return ResponseEntity.ok(relatorios);
    }

    @GetMapping("/cpf")
    public ResponseEntity<List<RelatorioDTO>> buscarRelatoriosPorCpf(@RequestParam String cpf) {
        List<RelatorioDTO> relatorios = relatorioService.buscarRelatoriosPorCpf(cpf);
        return ResponseEntity.ok(relatorios);
    }

    @PostMapping
    public ResponseEntity<RelatorioDTO> criarRelatorio(@RequestBody RelatorioDTO relatorioDTO) {
        try {
            RelatorioDTO relatorioCriado = relatorioService.criarRelatorio(relatorioDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(relatorioCriado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<RelatorioDTO> atualizarRelatorio(@PathVariable Long id, @RequestBody RelatorioDTO relatorioDTO) {
        try {
            RelatorioDTO relatorioAtualizado = relatorioService.atualizarRelatorio(id, relatorioDTO);
            return ResponseEntity.ok(relatorioAtualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirRelatorio(@PathVariable Long id) {
        try {
            relatorioService.excluirRelatorio(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/test")
    public ResponseEntity<String> testeEndpoint(@PathVariable Long id) {
        return ResponseEntity.ok("Endpoint funcionando para ID: " + id);
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> gerarPdf(@PathVariable Long id) {
        try {
            System.out.println("Gerando PDF para relatório ID: " + id);
            
            // Buscar o relatório
            Optional<RelatorioDTO> relatorioOpt = relatorioService.buscarRelatorioPorId(id);
            if (relatorioOpt.isEmpty()) {
                System.out.println("Relatório não encontrado: " + id);
                return ResponseEntity.notFound().build();
            }

            RelatorioDTO relatorio = relatorioOpt.get();
            System.out.println("Relatório encontrado: " + relatorio.getNomeCliente());

            // Buscar o tipo de contrato
            Optional<TipoContratoDTO> tipoContratoOpt = tipoContratoService.buscarTipoContratoPorId(relatorio.getTipoContratoId());
            if (tipoContratoOpt.isEmpty()) {
                System.out.println("Tipo de contrato não encontrado: " + relatorio.getTipoContratoId());
                return ResponseEntity.badRequest().build();
            }

            TipoContratoDTO tipoContrato = tipoContratoOpt.get();
            System.out.println("Tipo de contrato encontrado: " + tipoContrato.getTipo());

            // Gerar PDF
            byte[] pdfBytes = pdfService.gerarDocumento(relatorio, tipoContrato);
            
            // Configurar headers para download
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", 
                String.format("%s_%s_%s.pdf", 
                    tipoContrato.getTipo().toString().toLowerCase(),
                    relatorio.getNomeCliente().replaceAll("\\s+", "_"),
                    relatorio.getDataFesta().toString().replace("-", "_")
                ));

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            System.err.println("Erro ao gerar PDF: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}