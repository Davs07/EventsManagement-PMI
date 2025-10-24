package com.api.gestion.eventos.entities;

import com.api.gestion.eventos.enums.EstadoEvento;
import com.api.gestion.eventos.enums.TipoEvento;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Evento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String nombre;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoEvento tipoEvento;

    @Column(length = 300)
    private String ubicacion;

    @Column(nullable = false)
    private Long capacidadMaxima;

    @Column(nullable = false)
    private Boolean brindaCertificado = true;

    @Lob
    private byte[] plantillaImagen;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Column(name = "estado")
    @Enumerated(EnumType.STRING)
    private EstadoEvento estadoEvento = EstadoEvento.PROGRAMADO;

    @ManyToOne(fetch = FetchType.LAZY)
    private Evento eventoPadre;

    @OneToMany(mappedBy = "eventoPadre", cascade = CascadeType.ALL)
    private Set<Evento> eventosHijo = new HashSet<>();

    @OneToMany(mappedBy = "evento", cascade = CascadeType.ALL)
    private Set<Asistencia> asistencias = new HashSet<>();

    // ⚠️ equals y hashCode SOLO con id
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Evento)) return false;
        Evento evento = (Evento) o;
        return Objects.equals(id, evento.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
