package com.atom.contratoespaco.service;

import com.atom.contratoespaco.dto.EspacoDTO;
import com.atom.contratoespaco.entity.Espaco;
import com.atom.contratoespaco.repository.EspacoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class EspacoService {

    private final EspacoRepository espacoRepository;

    public List<EspacoDTO> listarEspacos() {
        return espacoRepository.findAll().stream()
                .map(EspacoDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public Optional<EspacoDTO> buscarEspacoPorId(Long id) {
        return espacoRepository.findById(id)
                .map(EspacoDTO::fromEntity);
    }

    public EspacoDTO criarEspaco(EspacoDTO espacoDTO) {
        if (espacoRepository.existsByNome(espacoDTO.getNome())) {
            throw new IllegalArgumentException("Já existe um espaço com este nome");
        }
        
        Espaco espaco = espacoDTO.toEntity();
        espaco.setId(null); // Garantir que é uma nova entidade
        Espaco espacoSalvo = espacoRepository.save(espaco);
        return EspacoDTO.fromEntity(espacoSalvo);
    }

    public EspacoDTO atualizarEspaco(Long id, EspacoDTO espacoDTO) {
        Espaco espacoExistente = espacoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Espaço não encontrado"));
        
        if (!espacoExistente.getNome().equals(espacoDTO.getNome()) && 
            espacoRepository.existsByNome(espacoDTO.getNome())) {
            throw new IllegalArgumentException("Já existe um espaço com este nome");
        }
        
        espacoExistente.setNome(espacoDTO.getNome());
        espacoExistente.setLogoUrl(espacoDTO.getLogoUrl());
        espacoExistente.setNomeProprietario(espacoDTO.getNomeProprietario());
        espacoExistente.setCnpjProprietario(espacoDTO.getCnpjProprietario());
        
        Espaco espacoAtualizado = espacoRepository.save(espacoExistente);
        return EspacoDTO.fromEntity(espacoAtualizado);
    }

    public void excluirEspaco(Long id) {
        if (!espacoRepository.existsById(id)) {
            throw new IllegalArgumentException("Espaço não encontrado");
        }
        espacoRepository.deleteById(id);
    }

    public List<EspacoDTO> buscarEspacosPorNome(String nome) {
        return espacoRepository.findByNomeContainingIgnoreCase(nome).stream()
                .map(EspacoDTO::fromEntity)
                .collect(Collectors.toList());
    }
}