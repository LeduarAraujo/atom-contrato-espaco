package com.atom.contratoespaco.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "espacos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Espaco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(name = "logo_url")
    private String logoUrl;

    @Lob
    @Column(name = "logo_data")
    private byte[] logoData;

    @Column(name = "logo_mime_type")
    private String logoMimeType;

    @Column(name = "nome_proprietario", length = 100)
    private String nomeProprietario;

    @Column(name = "cnpj_proprietario", length = 18)
    private String cnpjProprietario;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}