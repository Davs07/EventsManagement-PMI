package com.api.gestion.eventos.entities;

import com.api.gestion.eventos.enums.RolParticipante;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
    private RolParticipante rol;

    private LocalDateTime horaIngreso;

    @Lob
    private byte[] certificado;

    @Lob
    private String codigoQr;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaRegistro = LocalDateTime.now();

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @Column(nullable = false)
    private Boolean asistio = false;

    // ⚠️ equals y hashCode SOLO con id
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Asistencia)) return false;
        Asistencia that = (Asistencia) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
