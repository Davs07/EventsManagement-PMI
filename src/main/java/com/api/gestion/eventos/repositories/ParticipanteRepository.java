package com.api.gestion.eventos.repositories;

import com.api.gestion.eventos.entities.Participante;
import com.api.gestion.eventos.enums.RolParticipante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParticipanteRepository extends JpaRepository<Participante, Long> {
    boolean existsParticipanteByEmail(String email);

//    Participante findByEmail(String email);

    Optional<Participante> findByEmail(String email);

    @Query("SELECT DISTINCT p FROM Participante p " +
            "LEFT JOIN FETCH p.asistencias a " +
            "WHERE a.evento.id = :eventoId AND a.rol = :rol")
    List<Participante> findByEventoIdWithAsistencia(@Param("eventoId") Long eventoId, @Param("rol") RolParticipante rol);

    @Query("SELECT DISTINCT p FROM Participante p " +
            "LEFT JOIN FETCH p.asistencias a " +
            "WHERE a.evento.id = :eventoId AND a.rol = :rol")
    List<Participante> findByEventoIdAndRolWithAsistencia(@Param("eventoId") Long eventoId, @Param("rol") RolParticipante rol);
}
