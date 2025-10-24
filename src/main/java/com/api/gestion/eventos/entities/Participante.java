package com.api.gestion.eventos.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = "email", name = "uk_participante_email"),
        @UniqueConstraint(columnNames = "telefono", name = "uk_participante_telefono")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Participante {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombres;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String dni;
    private String email;
    private String numeroWhatsapp;
    private String ciudad;
    private String rol;
    private String gradoEstudio;

    @Lob
    private byte[] evidenciaEstudio;

    private String capituloPmi;
    private String idMiembroPmi;
    private boolean cuentaConCertificadoPmi;

    @OneToMany(mappedBy = "participante", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Asistencia> asistencias = new HashSet<>();

    // ⚠️ equals y hashCode SOLO con id
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Participante)) return false;
        Participante that = (Participante) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
