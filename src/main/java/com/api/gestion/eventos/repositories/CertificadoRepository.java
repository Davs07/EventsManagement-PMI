package com.api.gestion.eventos.repositories;

import com.api.gestion.eventos.entities.Certificado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CertificadoRepository extends JpaRepository<Certificado, Long> {
    boolean existsByCodigoCertificado(String codigoCertificado);
    Optional<Certificado> findByAsistencia_Id(Long asistenciaId);
    Optional<Certificado> findByCodigoCertificado(String codigoCertificado);
    List<Certificado> findByAsistencia_Evento_Id(Long eventoId);
}
