package com.api.gestion.eventos.mappers;

import com.api.gestion.eventos.dtos.AsistenciaDTO;
import com.api.gestion.eventos.entities.Asistencia;
import com.api.gestion.eventos.entities.Evento;
import com.api.gestion.eventos.entities.Participante;
import com.api.gestion.eventos.enums.RolParticipante;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
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
        
        // Parsear el rol del DTO si viene, validando que sea un valor válido del enum
        if (dto.getRol() != null && !dto.getRol().isEmpty()) {
            try {
                a.setRol(RolParticipante.valueOf(dto.getRol().toUpperCase()));
            } catch (IllegalArgumentException e) {
                // Si el rol no es válido, asignar ASISTENTE por defecto
                a.setRol(RolParticipante.ASISTENTE);
            }
        } else {
            // Si no viene rol, asignar ASISTENTE por defecto
            a.setRol(RolParticipante.ASISTENTE);
        }
        
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

    public static Asistencia crearAsistencia(Participante participante, Evento evento) {
        if (participante == null || evento == null) {
            return null;
        }

        return Asistencia.builder()
                .participante(participante)
                .evento(evento)
                .rol(RolParticipante.ASISTENTE)
                .fechaRegistro(LocalDateTime.now())
                .asistio(false)
                .codigoQr(UUID.randomUUID().toString())
                .build();
    }
}
