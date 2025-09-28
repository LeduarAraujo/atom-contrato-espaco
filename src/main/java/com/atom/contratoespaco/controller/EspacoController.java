package com.atom.contratoespaco.controller;

import com.atom.contratoespaco.dto.EspacoDTO;
import com.atom.contratoespaco.service.EspacoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/espacos")
@RequiredArgsConstructor
public class EspacoController {

    private final EspacoService espacoService;

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("API funcionando!");
    }

    @GetMapping
    public ResponseEntity<List<EspacoDTO>> listarEspacos() {
        try {
            List<EspacoDTO> espacos = espacoService.listarEspacos();
            return ResponseEntity.ok(espacos);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<EspacoDTO> buscarEspacoPorId(@PathVariable Long id) {
        return espacoService.buscarEspacoPorId(id)
                .map(espaco -> ResponseEntity.ok(espaco))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<EspacoDTO>> buscarEspacosPorNome(@RequestParam String nome) {
        List<EspacoDTO> espacos = espacoService.buscarEspacosPorNome(nome);
        return ResponseEntity.ok(espacos);
    }

    @PostMapping
    public ResponseEntity<EspacoDTO> criarEspaco(@RequestBody EspacoDTO espacoDTO) {
        try {
            EspacoDTO espacoCriado = espacoService.criarEspaco(espacoDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(espacoCriado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<EspacoDTO> atualizarEspaco(@PathVariable Long id, @RequestBody EspacoDTO espacoDTO) {
        try {
            EspacoDTO espacoAtualizado = espacoService.atualizarEspaco(id, espacoDTO);
            return ResponseEntity.ok(espacoAtualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirEspaco(@PathVariable Long id) {
        try {
            espacoService.excluirEspaco(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}