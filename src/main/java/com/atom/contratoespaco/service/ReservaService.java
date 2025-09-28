package com.atom.contratoespaco.service;

import com.atom.contratoespaco.dto.ReservaDTO;
import com.atom.contratoespaco.entity.Reserva;
import com.atom.contratoespaco.repository.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;

    public List<ReservaDTO> listarReservas() {
        return reservaRepository.findAll().stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    public Optional<ReservaDTO> buscarReservaPorId(Long id) {
        return reservaRepository.findById(id)
                .map(this::converterParaDTO);
    }

    public List<ReservaDTO> buscarReservasPorEspaco(Long espacoId) {
        return reservaRepository.findByEspacoId(espacoId).stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    public List<ReservaDTO> buscarReservasPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        return reservaRepository.findByDataFestaBetween(dataInicio, dataFim).stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    public List<ReservaDTO> buscarReservasPorNomeCliente(String nome) {
        return reservaRepository.findByNomeClienteContainingIgnoreCase(nome).stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    public List<ReservaDTO> buscarReservasPorCpf(String cpf) {
        return reservaRepository.findByCpfCliente(cpf).stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    public ReservaDTO criarReserva(ReservaDTO reservaDTO) {
        // Temporariamente desabilitar validação para debug
        // validarConflitosHorario(reservaDTO);
        
        Reserva reserva = converterParaEntidade(reservaDTO);
        reserva = reservaRepository.save(reserva);
        return converterParaDTO(reserva);
    }

    public Optional<ReservaDTO> atualizarReserva(Long id, ReservaDTO reservaDTO) {
        return reservaRepository.findById(id)
                .map(reservaExistente -> {
                    // Validar conflitos de horário (excluindo a própria reserva)
                    validarConflitosHorario(reservaDTO, id);
                    
                    reservaExistente.setEspacoId(reservaDTO.getEspacoId());
                    reservaExistente.setNomeCliente(reservaDTO.getNomeCliente());
                    reservaExistente.setCpfCliente(reservaDTO.getCpfCliente());
                    reservaExistente.setTelefoneCliente(reservaDTO.getTelefoneCliente());
                    reservaExistente.setDataFesta(reservaDTO.getDataFesta());
                    reservaExistente.setHoraInicio(reservaDTO.getHoraInicio());
                    reservaExistente.setHoraFim(reservaDTO.getHoraFim());
                    reservaExistente.setValorPagamento(reservaDTO.getValorPagamento());
                    reservaExistente.setValorIntegral(reservaDTO.getValorIntegral());
                    reservaExistente.setValorRestante(reservaDTO.getValorRestante());
                    
                    return converterParaDTO(reservaRepository.save(reservaExistente));
                });
    }

    public boolean excluirReserva(Long id) {
        if (reservaRepository.existsById(id)) {
            reservaRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private void validarConflitosHorario(ReservaDTO reservaDTO) {
        validarConflitosHorario(reservaDTO, null);
    }

    private void validarConflitosHorario(ReservaDTO reservaDTO, Long idExcluir) {
        // Validação temporariamente desabilitada para debug
        // List<Reserva> conflitos = reservaRepository.findConflitosHorario(
        //         reservaDTO.getEspacoId(),
        //         reservaDTO.getDataFesta(),
        //         reservaDTO.getHoraInicio(),
        //         reservaDTO.getHoraFim()
        // );

        // // Se estiver atualizando, remover a própria reserva da lista de conflitos
        // if (idExcluir != null) {
        //     conflitos = conflitos.stream()
        //             .filter(r -> !r.getId().equals(idExcluir))
        //             .collect(Collectors.toList());
        // }

        // if (!conflitos.isEmpty()) {
        //     throw new RuntimeException("Já existe uma reserva para este espaço no horário selecionado");
        // }
    }

    private ReservaDTO converterParaDTO(Reserva reserva) {
        ReservaDTO dto = new ReservaDTO();
        dto.setId(reserva.getId());
        dto.setEspacoId(reserva.getEspacoId());
        dto.setNomeCliente(reserva.getNomeCliente());
        dto.setCpfCliente(reserva.getCpfCliente());
        dto.setTelefoneCliente(reserva.getTelefoneCliente());
        dto.setDataFesta(reserva.getDataFesta());
        dto.setHoraInicio(reserva.getHoraInicio());
        dto.setHoraFim(reserva.getHoraFim());
        dto.setValorPagamento(reserva.getValorPagamento());
        dto.setValorIntegral(reserva.getValorIntegral());
        dto.setValorRestante(reserva.getValorRestante());
        dto.setCreatedAt(reserva.getCreatedAt());
        dto.setUpdatedAt(reserva.getUpdatedAt());
        return dto;
    }

    private Reserva converterParaEntidade(ReservaDTO dto) {
        Reserva reserva = new Reserva();
        reserva.setEspacoId(dto.getEspacoId());
        reserva.setNomeCliente(dto.getNomeCliente());
        reserva.setCpfCliente(dto.getCpfCliente());
        reserva.setTelefoneCliente(dto.getTelefoneCliente());
        reserva.setDataFesta(dto.getDataFesta());
        reserva.setHoraInicio(dto.getHoraInicio());
        reserva.setHoraFim(dto.getHoraFim());
        reserva.setValorPagamento(dto.getValorPagamento());
        reserva.setValorIntegral(dto.getValorIntegral() != null ? dto.getValorIntegral() : false);
        reserva.setValorRestante(dto.getValorRestante());
        return reserva;
    }
}
