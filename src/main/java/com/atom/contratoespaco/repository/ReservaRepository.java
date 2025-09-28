package com.atom.contratoespaco.repository;

import com.atom.contratoespaco.entity.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    List<Reserva> findByEspacoId(Long espacoId);

    List<Reserva> findByDataFestaBetween(LocalDate dataInicio, LocalDate dataFim);

    List<Reserva> findByNomeClienteContainingIgnoreCase(String nomeCliente);

    List<Reserva> findByCpfCliente(String cpfCliente);

    @Query("SELECT r FROM Reserva r WHERE r.espacoId = :espacoId AND r.dataFesta = :dataFesta")
    List<Reserva> findByEspacoIdAndDataFesta(@Param("espacoId") Long espacoId, @Param("dataFesta") LocalDate dataFesta);

    // Query temporariamente removida para debug
    // @Query("SELECT r FROM Reserva r WHERE r.espacoId = :espacoId AND r.dataFesta = :dataFesta AND " +
    //        "((r.horaInicio <= :horaInicio AND r.horaFim > :horaInicio) OR " +
    //        "(r.horaInicio < :horaFim AND r.horaFim >= :horaFim) OR " +
    //        "(r.horaInicio >= :horaInicio AND r.horaFim <= :horaFim))")
    // List<Reserva> findConflitosHorario(@Param("espacoId") Long espacoId, 
    //                                   @Param("dataFesta") LocalDate dataFesta,
    //                                   @Param("horaInicio") LocalTime horaInicio,
    //                                   @Param("horaFim") LocalTime horaFim);
}
