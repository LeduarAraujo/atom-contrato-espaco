package com.atom.contratoespaco.service;

import com.atom.contratoespaco.dto.RelatorioDTO;
import com.atom.contratoespaco.entity.Relatorio;
import com.atom.contratoespaco.entity.TipoContrato;
import com.atom.contratoespaco.repository.RelatorioRepository;
import com.atom.contratoespaco.repository.TipoContratoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RelatorioService {

    private final RelatorioRepository relatorioRepository;
    private final TipoContratoRepository tipoContratoRepository;

    public List<RelatorioDTO> listarRelatorios() {
        return relatorioRepository.findAll().stream()
                .map(RelatorioDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public Optional<RelatorioDTO> buscarRelatorioPorId(Long id) {
        return relatorioRepository.findById(id)
                .map(relatorio -> {
                    // Carregar o TipoContrato explicitamente
                    relatorio.getTipoContrato().getId();
                    return RelatorioDTO.fromEntity(relatorio);
                });
    }

    public List<RelatorioDTO> buscarRelatoriosPorTipoContrato(Long tipoContratoId) {
        return relatorioRepository.findByTipoContratoId(tipoContratoId).stream()
                .map(RelatorioDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<RelatorioDTO> buscarRelatoriosPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        return relatorioRepository.findByDataFestaBetween(dataInicio, dataFim).stream()
                .map(RelatorioDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<RelatorioDTO> buscarRelatoriosPorNomeCliente(String nome) {
        return relatorioRepository.findByNomeClienteContainingIgnoreCase(nome).stream()
                .map(RelatorioDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<RelatorioDTO> buscarRelatoriosPorCpf(String cpf) {
        return relatorioRepository.findByCpfCliente(cpf).stream()
                .map(RelatorioDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public RelatorioDTO criarRelatorio(RelatorioDTO relatorioDTO) {
        TipoContrato tipoContrato = tipoContratoRepository.findById(relatorioDTO.getTipoContratoId())
                .orElseThrow(() -> new IllegalArgumentException("Tipo de contrato não encontrado"));
        
        Relatorio relatorio = relatorioDTO.toEntity();
        relatorio.setId(null); // Garantir que é uma nova entidade
        relatorio.setTipoContrato(tipoContrato);
        
        Relatorio relatorioSalvo = relatorioRepository.save(relatorio);
        return RelatorioDTO.fromEntity(relatorioSalvo);
    }

    public RelatorioDTO atualizarRelatorio(Long id, RelatorioDTO relatorioDTO) {
        Relatorio relatorioExistente = relatorioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Relatório não encontrado"));
        
        TipoContrato tipoContrato = tipoContratoRepository.findById(relatorioDTO.getTipoContratoId())
                .orElseThrow(() -> new IllegalArgumentException("Tipo de contrato não encontrado"));
        
        relatorioExistente.setTipoContrato(tipoContrato);
        relatorioExistente.setNomeCliente(relatorioDTO.getNomeCliente());
        relatorioExistente.setCpfCliente(relatorioDTO.getCpfCliente());
        relatorioExistente.setDataFesta(relatorioDTO.getDataFesta());
        relatorioExistente.setHoraInicio(relatorioDTO.getHoraInicio());
        relatorioExistente.setHoraFim(relatorioDTO.getHoraFim());
        relatorioExistente.setValorPago(relatorioDTO.getValorPago());
        relatorioExistente.setValorIntegral(relatorioDTO.getValorIntegral());
        relatorioExistente.setArquivoGerado(relatorioDTO.getArquivoGerado());
        
        Relatorio relatorioAtualizado = relatorioRepository.save(relatorioExistente);
        return RelatorioDTO.fromEntity(relatorioAtualizado);
    }

    public void excluirRelatorio(Long id) {
        if (!relatorioRepository.existsById(id)) {
            throw new IllegalArgumentException("Relatório não encontrado");
        }
        relatorioRepository.deleteById(id);
    }
}
