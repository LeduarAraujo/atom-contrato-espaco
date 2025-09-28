package com.atom.contratoespaco.repository;

import com.atom.contratoespaco.entity.TipoContrato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TipoContratoRepository extends JpaRepository<TipoContrato, Long> {

    List<TipoContrato> findByEspacoId(Long espacoId);
    
    List<TipoContrato> findByTipo(TipoContrato.Tipo tipo);
    
    @Query("SELECT tc FROM TipoContrato tc WHERE tc.espaco.id = :espacoId AND tc.tipo = :tipo")
    Optional<TipoContrato> findByEspacoIdAndTipo(@Param("espacoId") Long espacoId, @Param("tipo") TipoContrato.Tipo tipo);
    
    @Query("SELECT tc FROM TipoContrato tc WHERE tc.espaco.id = :espacoId")
    List<TipoContrato> findAllByEspacoId(@Param("espacoId") Long espacoId);
}
