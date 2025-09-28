package com.atom.contratoespaco.repository;

import com.atom.contratoespaco.entity.Espaco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EspacoRepository extends JpaRepository<Espaco, Long> {

    @Query("SELECT e FROM Espaco e WHERE LOWER(e.nome) LIKE LOWER(CONCAT('%', :nome, '%'))")
    List<Espaco> findByNomeContainingIgnoreCase(@Param("nome") String nome);

    Optional<Espaco> findByNome(String nome);
    
    boolean existsByNome(String nome);
}