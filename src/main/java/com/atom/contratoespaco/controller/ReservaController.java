package com.atom.contratoespaco.controller;

import com.atom.contratoespaco.dto.ReservaDTO;
import com.atom.contratoespaco.service.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/reservas")
public class ReservaController {

    @Autowired
    private ReservaService reservaService;

    @GetMapping
    public ResponseEntity<List<ReservaDTO>> listarReservas() {
        try {
            System.out.println("=== DEBUG: Listando reservas ===");
            List<ReservaDTO> reservas = reservaService.listarReservas();
            System.out.println("Reservas encontradas: " + reservas.size());
            return ResponseEntity.ok(reservas);
        } catch (Exception e) {
            System.err.println("=== ERRO ao listar reservas ===");
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservaDTO> buscarReservaPorId(@PathVariable Long id) {
        try {
            Optional<ReservaDTO> reserva = reservaService.buscarReservaPorId(id);
            return reserva.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/espaco/{espacoId}")
    public ResponseEntity<List<ReservaDTO>> buscarReservasPorEspaco(@PathVariable Long espacoId) {
        try {
            List<ReservaDTO> reservas = reservaService.buscarReservasPorEspaco(espacoId);
            return ResponseEntity.ok(reservas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/periodo")
    public ResponseEntity<List<ReservaDTO>> buscarReservasPorPeriodo(
            @RequestParam String dataInicio,
            @RequestParam String dataFim) {
        try {
            LocalDate inicio = LocalDate.parse(dataInicio);
            LocalDate fim = LocalDate.parse(dataFim);
            List<ReservaDTO> reservas = reservaService.buscarReservasPorPeriodo(inicio, fim);
            return ResponseEntity.ok(reservas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/cliente")
    public ResponseEntity<List<ReservaDTO>> buscarReservasPorNomeCliente(@RequestParam String nome) {
        try {
            List<ReservaDTO> reservas = reservaService.buscarReservasPorNomeCliente(nome);
            return ResponseEntity.ok(reservas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/cpf")
    public ResponseEntity<List<ReservaDTO>> buscarReservasPorCpf(@RequestParam String cpf) {
        try {
            List<ReservaDTO> reservas = reservaService.buscarReservasPorCpf(cpf);
            return ResponseEntity.ok(reservas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<?> criarReserva(@RequestBody ReservaDTO reservaDTO) {
        try {
            System.out.println("=== DEBUG: Recebendo reserva ===");
            System.out.println("ReservaDTO recebido: " + reservaDTO);
            System.out.println("EspacoId: " + reservaDTO.getEspacoId());
            System.out.println("DataFesta: " + reservaDTO.getDataFesta());
            System.out.println("HoraInicio: " + reservaDTO.getHoraInicio());
            System.out.println("HoraFim: " + reservaDTO.getHoraFim());
            System.out.println("ValorPagamento: " + reservaDTO.getValorPagamento());
            System.out.println("ValorIntegral: " + reservaDTO.getValorIntegral());
            
            ReservaDTO reservaCriada = reservaService.criarReserva(reservaDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(reservaCriada);
        } catch (RuntimeException e) {
            System.err.println("=== ERRO RuntimeException ===");
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            System.err.println("=== ERRO Exception ===");
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarReserva(@PathVariable Long id, @RequestBody ReservaDTO reservaDTO) {
        try {
            Optional<ReservaDTO> reservaAtualizada = reservaService.atualizarReserva(id, reservaDTO);
            return reservaAtualizada.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirReserva(@PathVariable Long id) {
        try {
            boolean excluido = reservaService.excluirReserva(id);
            return excluido ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
