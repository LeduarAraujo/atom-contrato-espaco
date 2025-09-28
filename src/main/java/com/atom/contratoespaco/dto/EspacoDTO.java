package com.atom.contratoespaco.dto;

import com.atom.contratoespaco.entity.Espaco;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EspacoDTO {
    
    private Long id;
    private String nome;
    private String logoUrl;
    private byte[] logoData;
    private String logoMimeType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static EspacoDTO fromEntity(Espaco espaco) {
        if (espaco == null) return null;
        
        EspacoDTO dto = new EspacoDTO();
        dto.setId(espaco.getId());
        dto.setNome(espaco.getNome());
        dto.setLogoUrl(espaco.getLogoUrl());
        dto.setLogoData(espaco.getLogoData());
        dto.setLogoMimeType(espaco.getLogoMimeType());
        dto.setCreatedAt(espaco.getCreatedAt());
        dto.setUpdatedAt(espaco.getUpdatedAt());
        return dto;
    }

    public Espaco toEntity() {
        Espaco espaco = new Espaco();
        espaco.setId(this.id);
        espaco.setNome(this.nome);
        espaco.setLogoUrl(this.logoUrl);
        espaco.setLogoData(this.logoData);
        espaco.setLogoMimeType(this.logoMimeType);
        espaco.setCreatedAt(this.createdAt);
        espaco.setUpdatedAt(this.updatedAt);
        return espaco;
    }
}