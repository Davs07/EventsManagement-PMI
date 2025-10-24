package com.api.gestion.eventos.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParticipanteDto {
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
    // Base64 o null
    private String evidenciaEstudio;
    private String capituloPmi;
    private String idMiembroPmi;
    private Boolean cuentaConCertificadoPmi;
    // Lista de asistencias en forma resumida para evitar referencia circular
    private List<AsistenciaDTO> asistencias;
}
