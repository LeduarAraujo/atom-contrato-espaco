package com.atom.contratoespaco.controller;

import com.atom.contratoespaco.dto.TipoContratoDTO;
import com.atom.contratoespaco.entity.TipoContrato;
import com.atom.contratoespaco.service.TipoContratoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tipos-contrato")
@RequiredArgsConstructor
public class TipoContratoController {

    private final TipoContratoService tipoContratoService;

    @GetMapping
    public ResponseEntity<List<TipoContratoDTO>> listarTiposContrato() {
        List<TipoContratoDTO> tiposContrato = tipoContratoService.listarTiposContrato();
        return ResponseEntity.ok(tiposContrato);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoContratoDTO> buscarTipoContratoPorId(@PathVariable Long id) {
        return tipoContratoService.buscarTipoContratoPorId(id)
                .map(tipoContrato -> ResponseEntity.ok(tipoContrato))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/espaco/{espacoId}")
    public ResponseEntity<List<TipoContratoDTO>> buscarTiposContratoPorEspaco(@PathVariable Long espacoId) {
        List<TipoContratoDTO> tiposContrato = tipoContratoService.buscarTiposContratoPorEspaco(espacoId);
        return ResponseEntity.ok(tiposContrato);
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<TipoContratoDTO>> buscarTiposContratoPorTipo(@PathVariable TipoContrato.Tipo tipo) {
        List<TipoContratoDTO> tiposContrato = tipoContratoService.buscarTiposContratoPorTipo(tipo);
        return ResponseEntity.ok(tiposContrato);
    }

    @PostMapping
    public ResponseEntity<TipoContratoDTO> criarTipoContrato(@RequestBody TipoContratoDTO tipoContratoDTO) {
        try {
            TipoContratoDTO tipoContratoCriado = tipoContratoService.criarTipoContrato(tipoContratoDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(tipoContratoCriado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<TipoContratoDTO> atualizarTipoContrato(@PathVariable Long id, @RequestBody TipoContratoDTO tipoContratoDTO) {
        try {
            TipoContratoDTO tipoContratoAtualizado = tipoContratoService.atualizarTipoContrato(id, tipoContratoDTO);
            return ResponseEntity.ok(tipoContratoAtualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirTipoContrato(@PathVariable Long id) {
        try {
            tipoContratoService.excluirTipoContrato(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
