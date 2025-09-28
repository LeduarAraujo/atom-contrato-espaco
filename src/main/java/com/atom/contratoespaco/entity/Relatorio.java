package com.atom.contratoespaco.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "relatorios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Relatorio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_contrato_id", nullable = false)
    private TipoContrato tipoContrato;

    @Column(name = "nome_cliente", nullable = false, length = 100)
    private String nomeCliente;

    @Column(name = "cpf_cliente", nullable = false, length = 14)
    private String cpfCliente;

    @Column(name = "data_festa", nullable = false)
    private LocalDate dataFesta;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fim", nullable = false)
    private LocalTime horaFim;

    @Column(name = "valor_pago", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorPago;

    @Column(name = "valor_integral", nullable = false)
    private Boolean valorIntegral = false;

    @Column(name = "arquivo_gerado", length = 500)
    private String arquivoGerado;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
