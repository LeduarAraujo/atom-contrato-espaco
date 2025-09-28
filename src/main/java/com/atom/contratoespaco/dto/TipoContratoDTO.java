package com.atom.contratoespaco.dto;

import com.atom.contratoespaco.entity.TipoContrato;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TipoContratoDTO {
    
    private Long id;
    private Long espacoId;
    private String espacoNome;
    private TipoContrato.Tipo tipo;
    private String titulo;
    private String textoTemplate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static TipoContratoDTO fromEntity(TipoContrato tipoContrato) {
        if (tipoContrato == null) return null;
        
        TipoContratoDTO dto = new TipoContratoDTO();
        dto.setId(tipoContrato.getId());
        dto.setEspacoId(tipoContrato.getEspaco() != null ? tipoContrato.getEspaco().getId() : null);
        dto.setEspacoNome(tipoContrato.getEspaco() != null ? tipoContrato.getEspaco().getNome() : null);
        dto.setTipo(tipoContrato.getTipo());
        dto.setTitulo(tipoContrato.getTitulo());
        dto.setTextoTemplate(tipoContrato.getTextoTemplate());
        dto.setCreatedAt(tipoContrato.getCreatedAt());
        dto.setUpdatedAt(tipoContrato.getUpdatedAt());
        return dto;
    }

    public TipoContrato toEntity() {
        TipoContrato tipoContrato = new TipoContrato();
        tipoContrato.setId(this.id);
        tipoContrato.setTipo(this.tipo);
        tipoContrato.setTitulo(this.titulo);
        tipoContrato.setTextoTemplate(this.textoTemplate);
        tipoContrato.setCreatedAt(this.createdAt);
        tipoContrato.setUpdatedAt(this.updatedAt);
        return tipoContrato;
    }
}
