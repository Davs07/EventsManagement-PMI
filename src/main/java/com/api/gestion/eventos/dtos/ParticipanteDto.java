package com.api.gestion.eventos.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParticipanteDto {
    private Long id;
    private String nombres;
    private String apellidos;
    private String email;
    private String telefono;
}
