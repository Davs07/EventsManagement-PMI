package com.api.gestion.eventos.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_invitacion")
public class EmailConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Campos en español usados por el controlador y los DTOs
    private String asunto;
    @Column(length = 4000)
    private String mensaje;
    private String flyerPath; // ruta opcional al flyer/adjunto


    // Usamos ZonedDateTime para manejar zonas horarias correctamente
    private ZonedDateTime inicio;
    private ZonedDateTime fin;
    private String lugar;

    // Mantener también getters con nombres en inglés por si hay código previo que los usa
    public String getSubject() { return asunto; }
    public String getMessage() { return mensaje; }
    public String getAttachmentPath() { return flyerPath; }
    public ZonedDateTime getStart() { return inicio; }
    public ZonedDateTime getEnd() { return fin; }
    public String getLocation() { return lugar; }
}