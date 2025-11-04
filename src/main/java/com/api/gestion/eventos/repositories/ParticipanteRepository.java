package com.api.gestion.eventos.repositories;

import com.api.gestion.eventos.entities.Participante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParticipanteRepository extends JpaRepository<Participante, Long> {
    boolean existsParticipanteByEmail(String email);

//    Participante findByEmail(String email);

    Optional<Participante> findByEmail(String email);
}
