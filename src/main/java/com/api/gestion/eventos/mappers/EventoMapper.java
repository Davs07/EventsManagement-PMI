package com.api.gestion.eventos.mappers;

import com.api.gestion.eventos.dtos.EventoDTO;
import com.api.gestion.eventos.entities.Evento;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

public class EventoMapper {

    public static EventoDTO toDto(Evento e) {
        if (e == null) return null;
        EventoDTO dto = new EventoDTO();
        dto.setId(e.getId());
        dto.setNombre(e.getNombre());
        dto.setDescripcion(e.getDescripcion());
        dto.setFechaInicio(e.getFechaInicio());
        dto.setFechaFin(e.getFechaFin());
        dto.setTipoEvento(e.getTipoEvento() != null ? e.getTipoEvento().name() : null);
        dto.setUbicacion(e.getUbicacion());
        dto.setCapacidadMaxima(e.getCapacidadMaxima());
        dto.setBrindaCertificado(e.getBrindaCertificado());
        if (e.getPlantillaImagen() != null) {
            dto.setPlantillaImagen(Base64.getEncoder().encodeToString(e.getPlantillaImagen()));
        }
        dto.setFechaCreacion(e.getFechaCreacion());
        dto.setEstadoEvento(e.getEstadoEvento() != null ? e.getEstadoEvento().name() : null);

        if (e.getEventoPadre() != null) {
            dto.setEventoPadreId(e.getEventoPadre().getId());
        }
        if (e.getEventosHijo() != null && !e.getEventosHijo().isEmpty()) {
            dto.setEventosHijoIds(e.getEventosHijo().stream()
                    .map(Evento::getId)
                    .collect(Collectors.toList()));
        }

        // No incluir asistencias para evitar referencia circular
        return dto;
    }

    public static List<EventoDTO> toDtoList(List<Evento> list) {
        if (list == null) return null;
        return list.stream().map(EventoMapper::toDto).collect(Collectors.toList());
    }
}

