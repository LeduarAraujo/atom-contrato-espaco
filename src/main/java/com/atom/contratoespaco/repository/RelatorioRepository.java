package com.atom.contratoespaco.repository;

import com.atom.contratoespaco.entity.Relatorio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RelatorioRepository extends JpaRepository<Relatorio, Long> {

    List<Relatorio> findByTipoContratoId(Long tipoContratoId);
    
    @Query("SELECT r FROM Relatorio r WHERE r.dataFesta BETWEEN :dataInicio AND :dataFim")
    List<Relatorio> findByDataFestaBetween(@Param("dataInicio") LocalDate dataInicio, @Param("dataFim") LocalDate dataFim);
    
    @Query("SELECT r FROM Relatorio r WHERE LOWER(r.nomeCliente) LIKE LOWER(CONCAT('%', :nome, '%'))")
    List<Relatorio> findByNomeClienteContainingIgnoreCase(@Param("nome") String nome);
    
    @Query("SELECT r FROM Relatorio r WHERE r.cpfCliente = :cpf")
    List<Relatorio> findByCpfCliente(@Param("cpf") String cpf);
}
