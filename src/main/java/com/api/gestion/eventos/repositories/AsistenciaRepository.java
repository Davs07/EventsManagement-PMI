package com.api.gestion.eventos.repositories;

import com.api.gestion.eventos.entities.Asistencia;
import com.api.gestion.eventos.entities.Evento;
import com.api.gestion.eventos.entities.Participante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AsistenciaRepository extends JpaRepository<Asistencia, Long> {

    List<Asistencia> findByParticipante(Participante participante);
    List<Asistencia> findByEvento(Evento evento);
    Optional<Asistencia> findByCodigoQr(String codigoQr);
    boolean existsByParticipanteIdAndEventoId(Long participanteId, Long eventoId);

    //Metodo para buscar participantes por evento:
    @Query("SELECT DISTINCT a.participante FROM Asistencia a WHERE a.evento.id = :eventoId")
    List<Participante> findParticipantesByEventoId(@Param("eventoId") Long eventoId);

    //Metodo para buscar asistencia por participante y evento para cambiar estado de asistencia a true o false
    @Query("SELECT a FROM Asistencia a WHERE a.participante.id = :participanteId AND a.evento.id = :eventoId")
    Optional<Asistencia> findByParticipanteIdAndEventoId(@Param("participanteId") Long participanteId, @Param("eventoId") Long eventoId);
}
