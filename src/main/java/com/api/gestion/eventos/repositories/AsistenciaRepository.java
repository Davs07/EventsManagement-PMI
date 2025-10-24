package com.api.gestion.eventos.repositories;

import com.api.gestion.eventos.entities.Asistencia;
import com.api.gestion.eventos.entities.Evento;
import com.api.gestion.eventos.entities.Participante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AsistenciaRepository extends JpaRepository<Asistencia, Long> {

    List<Asistencia> findByParticipante(Participante participante);
    List<Asistencia> findByEvento(Evento evento);
    Optional<Asistencia> findByCodigoQr(String codigoQr);
}
