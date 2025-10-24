package com.api.gestion.eventos.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AsistenciaDTO {
    private Long id;

    // Referencia mínima al participante
    private Long participanteId;
    private String participanteNombre; // opcional, útil en respuestas

    // Referencia mínima al evento
    private Long eventoId;
    private String eventoNombre; // opcional

    private String rol; // nombre del enum RolParticipante
    private LocalDateTime horaIngreso;
    private String certificado; // Base64 o null
    private String codigoQr;
    private LocalDateTime fechaRegistro;
    private String observaciones;
    private Boolean asistio;
}
