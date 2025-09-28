package com.atom.contratoespaco.service;

import com.atom.contratoespaco.dto.TipoContratoDTO;
import com.atom.contratoespaco.entity.Espaco;
import com.atom.contratoespaco.entity.TipoContrato;
import com.atom.contratoespaco.repository.EspacoRepository;
import com.atom.contratoespaco.repository.TipoContratoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TipoContratoService {

    private final TipoContratoRepository tipoContratoRepository;
    private final EspacoRepository espacoRepository;

    public List<TipoContratoDTO> listarTiposContrato() {
        return tipoContratoRepository.findAll().stream()
                .map(TipoContratoDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public Optional<TipoContratoDTO> buscarTipoContratoPorId(Long id) {
        return tipoContratoRepository.findById(id)
                .map(TipoContratoDTO::fromEntity);
    }

    public List<TipoContratoDTO> buscarTiposContratoPorEspaco(Long espacoId) {
        return tipoContratoRepository.findByEspacoId(espacoId).stream()
                .map(TipoContratoDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<TipoContratoDTO> buscarTiposContratoPorTipo(TipoContrato.Tipo tipo) {
        return tipoContratoRepository.findByTipo(tipo).stream()
                .map(TipoContratoDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public TipoContratoDTO criarTipoContrato(TipoContratoDTO tipoContratoDTO) {
        Espaco espaco = espacoRepository.findById(tipoContratoDTO.getEspacoId())
                .orElseThrow(() -> new IllegalArgumentException("Espaço não encontrado"));
        
        // Verificar se já existe um tipo de contrato para este espaço e tipo
        if (tipoContratoRepository.findByEspacoIdAndTipo(tipoContratoDTO.getEspacoId(), tipoContratoDTO.getTipo()).isPresent()) {
            throw new IllegalArgumentException("Já existe um " + tipoContratoDTO.getTipo().name() + " para este espaço");
        }
        
        TipoContrato tipoContrato = tipoContratoDTO.toEntity();
        tipoContrato.setId(null); // Garantir que é uma nova entidade
        tipoContrato.setEspaco(espaco);
        
        TipoContrato tipoContratoSalvo = tipoContratoRepository.save(tipoContrato);
        return TipoContratoDTO.fromEntity(tipoContratoSalvo);
    }

    public TipoContratoDTO atualizarTipoContrato(Long id, TipoContratoDTO tipoContratoDTO) {
        TipoContrato tipoContratoExistente = tipoContratoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tipo de contrato não encontrado"));
        
        Espaco espaco = espacoRepository.findById(tipoContratoDTO.getEspacoId())
                .orElseThrow(() -> new IllegalArgumentException("Espaço não encontrado"));
        
        // Verificar se já existe outro tipo de contrato para este espaço e tipo (excluindo o atual)
        Optional<TipoContrato> tipoExistente = tipoContratoRepository.findByEspacoIdAndTipo(tipoContratoDTO.getEspacoId(), tipoContratoDTO.getTipo());
        if (tipoExistente.isPresent() && !tipoExistente.get().getId().equals(id)) {
            throw new IllegalArgumentException("Já existe um " + tipoContratoDTO.getTipo().name() + " para este espaço");
        }
        
        tipoContratoExistente.setEspaco(espaco);
        tipoContratoExistente.setTipo(tipoContratoDTO.getTipo());
        tipoContratoExistente.setTextoTemplate(tipoContratoDTO.getTextoTemplate());
        
        TipoContrato tipoContratoAtualizado = tipoContratoRepository.save(tipoContratoExistente);
        return TipoContratoDTO.fromEntity(tipoContratoAtualizado);
    }

    public void excluirTipoContrato(Long id) {
        if (!tipoContratoRepository.existsById(id)) {
            throw new IllegalArgumentException("Tipo de contrato não encontrado");
        }
        tipoContratoRepository.deleteById(id);
    }
}
