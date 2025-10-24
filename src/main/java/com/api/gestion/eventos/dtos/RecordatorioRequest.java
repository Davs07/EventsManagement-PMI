package com.api.gestion.eventos.dtos;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class RecordatorioRequest {
    private String asunto;
    private String mensaje;
    private String flyerPath;
    private String resumenEvento;
    private String descripcionEvento;
    private ZonedDateTime inicio;
    private ZonedDateTime fin;
    private String lugar;
}

