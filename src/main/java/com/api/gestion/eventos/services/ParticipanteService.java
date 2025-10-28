package com.api.gestion.eventos.services;

import com.api.gestion.eventos.dtos.ParticipanteDto;

import java.util.List;

public interface ParticipanteService {
    ParticipanteDto crear (ParticipanteDto participanteDto);
    List<ParticipanteDto> listar ();
    ParticipanteDto buscarporId (Long id);
    ParticipanteDto actualizar (ParticipanteDto participanteDto);
    void eliminarporId (Long id);

    //Listar participantes por evento
    List<ParticipanteDto> listarPorEvento(Long eventoId);
}
