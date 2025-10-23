package com.api.gestion.eventos.mappers;

import com.api.gestion.eventos.dtos.ParticipanteDto;
import com.api.gestion.eventos.entities.Participante;
import org.springframework.beans.BeanUtils;

import java.util.List;

public class ParticipanteMapper {

    public static ParticipanteDto mapearaDto (Participante participante) {
        ParticipanteDto participanteDto = new ParticipanteDto();
        BeanUtils.copyProperties(participante, participanteDto);
        return participanteDto;
    }

    public static Participante mapearaEntidad (ParticipanteDto participanteDto) {
        Participante participante = new Participante();
        BeanUtils.copyProperties(participanteDto, participante);
        return participante;
    }

    public static List<ParticipanteDto> mapearaListaDto (List<Participante> participantes) {
        return participantes.stream().map(participante -> mapearaDto (participante)).toList();
    }
}
