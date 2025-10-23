package com.api.gestion.eventos.entities;

import com.api.gestion.eventos.enums.RolParticipante;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Asistencia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Participante participante;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Evento evento;

    @Enumerated(EnumType.STRING)
    private RolParticipante rol; //Como participa en el evento

    private LocalDateTime horaIngreso;

    @Lob
    private byte[] certificado;

    @Lob
    private byte[] codigoQr;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaRegistro = LocalDateTime.now();

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @Column(nullable = false)
    private Boolean asistio = false;
}
