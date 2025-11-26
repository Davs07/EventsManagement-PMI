package com.api.gestion.eventos.repositories;

import com.api.gestion.eventos.entities.Asistencia;
import com.api.gestion.eventos.entities.Evento;
import com.api.gestion.eventos.entities.Participante;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AsistenciaRepository extends JpaRepository<Asistencia, Long> {

    // ✅ Optimizado: Carga TODO en una sola query
    @Query("SELECT DISTINCT a FROM Asistencia a " +
            "JOIN FETCH a.participante p " +
            "JOIN FETCH a.evento e " +
            "WHERE a.evento.id = :eventoId")
    List<Asistencia> findByEvento(@Param("eventoId") Long eventoId);

    // ✅ Para obtener solo participantes sin duplicados
    @Query("SELECT DISTINCT p FROM Participante p " +
            "INNER JOIN Asistencia a ON a.participante.id = p.id " +
            "WHERE a.evento.id = :eventoId " +
            "ORDER BY p.apellidoPaterno")
    List<Participante> findParticipantesByEventoId(@Param("eventoId") Long eventoId);

    // ✅ Otras queries optimizadas
    @Query("SELECT a FROM Asistencia a " +
            "JOIN FETCH a.participante " +
            "JOIN FETCH a.evento " +
            "WHERE a.participante.id = :participanteId")
    List<Asistencia> findByParticipante(@Param("participanteId") Long participanteId);

    @Query("SELECT a FROM Asistencia a " +
            "JOIN FETCH a.participante " +
            "JOIN FETCH a.evento " +
            "WHERE a.codigoQr = :codigoQr")
    Optional<Asistencia> findByCodigoQr(@Param("codigoQr") String codigoQr);

    @Query("SELECT a FROM Asistencia a " +
            "JOIN FETCH a.participante " +
            "JOIN FETCH a.evento " +
            "WHERE a.participante.id = :participanteId " +
            "AND a.evento.id = :eventoId")
    Optional<Asistencia> findByParticipanteIdAndEventoId(
            @Param("participanteId") Long participanteId,
            @Param("eventoId") Long eventoId);

    @Query("SELECT a FROM Asistencia a " +
            "JOIN FETCH a.participante " +
            "JOIN FETCH a.evento e " +
            "WHERE e.id = :eventoId AND a.asistio = :asistio")
    List<Asistencia> findByEvento_IdAndAsistio(
            @Param("eventoId") Long eventoId,
            @Param("asistio") Boolean asistio);

    boolean existsByParticipanteAndEvento(Participante participante, Evento evento);
    boolean existsByParticipanteIdAndEventoId(Long participanteId, Long eventoId);
}
