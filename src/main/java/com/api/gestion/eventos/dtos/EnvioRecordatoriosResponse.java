package com.api.gestion.eventos.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnvioRecordatoriosResponse {
    private int totalParticipantes;
    private int totalEnviados;
    private int totalFallidos;
    private List<String> errores;
}

