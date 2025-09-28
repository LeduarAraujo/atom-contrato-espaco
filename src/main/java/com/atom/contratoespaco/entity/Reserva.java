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
@Table(name = "reservas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "espaco_id", nullable = false)
    private Long espacoId;

    @Column(name = "nome_cliente", nullable = false, length = 100)
    private String nomeCliente;

    @Column(name = "cpf_cliente", nullable = false, length = 14)
    private String cpfCliente;

    @Column(name = "telefone_cliente", nullable = false, length = 20)
    private String telefoneCliente;

    @Column(name = "data_festa", nullable = false)
    private LocalDate dataFesta;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fim", nullable = false)
    private LocalTime horaFim;

    @Column(name = "valor_pagamento", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorPagamento;

    @Column(name = "valor_integral")
    private Boolean valorIntegral;

    @Column(name = "valor_restante", precision = 10, scale = 2)
    private BigDecimal valorRestante;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
