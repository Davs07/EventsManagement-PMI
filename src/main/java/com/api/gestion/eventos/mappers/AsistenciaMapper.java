package com.api.gestion.eventos.mappers;

import com.api.gestion.eventos.dtos.AsistenciaDTO;
import com.api.gestion.eventos.entities.Asistencia;
import com.api.gestion.eventos.entities.Evento;
import com.api.gestion.eventos.entities.Participante;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

public class AsistenciaMapper {

    public static AsistenciaDTO toDto(Asistencia a) {
        if (a == null) return null;
        AsistenciaDTO dto = new AsistenciaDTO();
        dto.setId(a.getId());
        if (a.getParticipante() != null) {
            dto.setParticipanteId(a.getParticipante().getId());
            dto.setParticipanteNombre(a.getParticipante().getNombres());
        }
        if (a.getEvento() != null) {
            dto.setEventoId(a.getEvento().getId());
            dto.setEventoNombre(a.getEvento().getNombre());
        }
        dto.setRol(a.getRol() != null ? a.getRol().name() : null);
        dto.setHoraIngreso(a.getHoraIngreso());
        dto.setFechaRegistro(a.getFechaRegistro());
        dto.setObservaciones(a.getObservaciones());
        dto.setAsistio(a.getAsistio());
        dto.setCodigoQr(a.getCodigoQr());
        if (a.getCertificado() != null) {
            dto.setCertificado(Base64.getEncoder().encodeToString(a.getCertificado()));
        } else {
            dto.setCertificado(null);
        }
        return dto;
    }

    public static Asistencia toEntity(AsistenciaDTO dto) {
        if (dto == null) return null;
        Asistencia a = new Asistencia();
        a.setId(dto.getId());
        if (dto.getParticipanteId() != null) {
            Participante p = new Participante();
            p.setId(dto.getParticipanteId());
            a.setParticipante(p);
        }
        if (dto.getEventoId() != null) {
            Evento e = new Evento();
            e.setId(dto.getEventoId());
            a.setEvento(e);
        }
        // rol se maneja por nombre del enum en servicio si es necesario
        // aquí no parseamos el enum para dejar que el servicio/validaciones lo manejen
        // pero si se desea, se puede parsear con RolParticipante.valueOf(...)
        a.setHoraIngreso(dto.getHoraIngreso());
        a.setObservaciones(dto.getObservaciones());
        a.setAsistio(dto.getAsistio() != null ? dto.getAsistio() : false);
        if (dto.getCertificado() != null) {
            a.setCertificado(Base64.getDecoder().decode(dto.getCertificado()));
        }
        a.setCodigoQr(dto.getCodigoQr());
        return a;
    }

    public static List<AsistenciaDTO> toDtoList(List<Asistencia> list) {
        return list.stream().map(AsistenciaMapper::toDto).collect(Collectors.toList());
    }
}
