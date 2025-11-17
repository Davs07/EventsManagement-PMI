package com.api.gestion.eventos.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "certificados")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Certificado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String codigoCertificado; // UUID único

    @ManyToOne
    @JoinColumn(name = "asistencia_id")
    private Asistencia asistencia;

    @Column(nullable = false)
    private LocalDateTime fechaEmision;

    @Column(nullable = false)
    private Boolean enviado = true;

//    @Lob        ESTE NO SE USA PORQUE OCUPA MUCHO ESPACIO EN LA BD
//    @Column(columnDefinition = "LONGBLOB")
//    private byte[] certificadoPdf; // PDF generado (opcional)

    private String emailEnviado;
}