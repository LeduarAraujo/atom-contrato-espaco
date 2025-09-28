package com.atom.contratoespaco.dto;

import com.atom.contratoespaco.entity.Relatorio;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RelatorioDTO {
    
    private Long id;
    private Long tipoContratoId;
    private String nomeCliente;
    private String cpfCliente;
    private LocalDate dataFesta;
    private LocalTime horaInicio;
    private LocalTime horaFim;
    private BigDecimal valorPago;
    private Boolean valorIntegral;
    private String arquivoGerado;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static RelatorioDTO fromEntity(Relatorio relatorio) {
        if (relatorio == null) return null;
        
        RelatorioDTO dto = new RelatorioDTO();
        dto.setId(relatorio.getId());
        dto.setTipoContratoId(relatorio.getTipoContrato() != null ? relatorio.getTipoContrato().getId() : null);
        dto.setNomeCliente(relatorio.getNomeCliente());
        dto.setCpfCliente(relatorio.getCpfCliente());
        dto.setDataFesta(relatorio.getDataFesta());
        dto.setHoraInicio(relatorio.getHoraInicio());
        dto.setHoraFim(relatorio.getHoraFim());
        dto.setValorPago(relatorio.getValorPago());
        dto.setValorIntegral(relatorio.getValorIntegral());
        dto.setArquivoGerado(relatorio.getArquivoGerado());
        dto.setCreatedAt(relatorio.getCreatedAt());
        dto.setUpdatedAt(relatorio.getUpdatedAt());
        return dto;
    }

    public Relatorio toEntity() {
        Relatorio relatorio = new Relatorio();
        relatorio.setId(this.id);
        relatorio.setNomeCliente(this.nomeCliente);
        relatorio.setCpfCliente(this.cpfCliente);
        relatorio.setDataFesta(this.dataFesta);
        relatorio.setHoraInicio(this.horaInicio);
        relatorio.setHoraFim(this.horaFim);
        relatorio.setValorPago(this.valorPago);
        relatorio.setValorIntegral(this.valorIntegral);
        relatorio.setArquivoGerado(this.arquivoGerado);
        relatorio.setCreatedAt(this.createdAt);
        relatorio.setUpdatedAt(this.updatedAt);
        return relatorio;
    }
}
