package com.api.gestion.eventos.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventoDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private String tipoEvento; // nombre del enum
    private String ubicacion;
    private Long capacidadMaxima;
    private Boolean brindaCertificado;
    private String plantillaImagen; // Base64 o null
    private LocalDateTime fechaCreacion;
    private String estadoEvento; // nombre del enum

    // Referencias por id para evitar ciclos
    private Long eventoPadreId;
    private List<Long> eventosHijoIds;

    // NO incluir asistencias para evitar referencia circular
}
