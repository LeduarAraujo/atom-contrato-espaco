package com.atom.contratoespaco.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "tipos_contrato")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TipoContrato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "espaco_id", nullable = false)
    private Espaco espaco;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private Tipo tipo;

    @Column(name = "texto_template", nullable = false, columnDefinition = "TEXT")
    private String textoTemplate;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum Tipo {
        CONTRATO, RECIBO
    }
}
