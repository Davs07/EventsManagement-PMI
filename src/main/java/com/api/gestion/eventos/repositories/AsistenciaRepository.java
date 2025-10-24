package com.api.gestion.eventos.repositories;

import com.api.gestion.eventos.entities.Asistencia;
import com.api.gestion.eventos.entities.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AsistenciaRepository extends JpaRepository<Asistencia, Long> {
}
