package com.api.gestion.eventos.entities;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParticipanteConAsistenciaDTO {
    private Long id;
    private String nombres;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String dni;
    private String email;
    private String telefono;
    private AsistenciaDTO asistencia;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AsistenciaDTO {
        private Boolean asistio;
        private LocalDateTime horaIngreso;
    }
}