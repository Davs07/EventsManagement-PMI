package com.api.gestion.eventos.mappers;

import com.api.gestion.eventos.dtos.ParticipanteDto;
import com.api.gestion.eventos.entities.Participante;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

public class ParticipanteMapper {

    public static ParticipanteDto mapearaDto(Participante participante) {
        if (participante == null) return null;

        ParticipanteDto dto = new ParticipanteDto();
        dto.setId(participante.getId());
        dto.setNombres(participante.getNombres());
        dto.setApellidoPaterno(participante.getApellidoPaterno());
        dto.setApellidoMaterno(participante.getApellidoMaterno());
        dto.setDni(participante.getDni());
        dto.setEmail(participante.getEmail());
        dto.setNumeroWhatsapp(participante.getNumeroWhatsapp());
        dto.setCiudad(participante.getCiudad());
        dto.setRol(participante.getRol());
        dto.setGradoEstudio(participante.getGradoEstudio());
        dto.setCapituloPmi(participante.getCapituloPmi());
        dto.setIdMiembroPmi(participante.getIdMiembroPmi());
        dto.setCuentaConCertificadoPmi(participante.isCuentaConCertificadoPmi());

        // Convertir byte[] a Base64
        if (participante.getEvidenciaEstudio() != null) {
            dto.setEvidenciaEstudio(Base64.getEncoder().encodeToString(participante.getEvidenciaEstudio()));
        }
        // LO COMENTE PARA QUE NO APAREZCA LA LISTA AL LISTAR PARTICIPANTES
        // Convertir asistencias a DTOs (evitando ciclos)
//        if (participante.getAsistencias() != null) {
//            dto.setAsistencias(
//                    participante.getAsistencias().stream()
//                            .map(AsistenciaMapper::toDto)
//                            .collect(Collectors.toList())
//            );
//        }

        return dto;
    }

    public static Participante mapearaEntidad(ParticipanteDto dto) {
        if (dto == null) return null;

        Participante participante = new Participante();
        participante.setId(dto.getId());
        participante.setNombres(dto.getNombres());
        participante.setApellidoPaterno(dto.getApellidoPaterno());
        participante.setApellidoMaterno(dto.getApellidoMaterno());
        participante.setDni(dto.getDni());
        participante.setEmail(dto.getEmail());
        participante.setNumeroWhatsapp(dto.getNumeroWhatsapp());
        participante.setCiudad(dto.getCiudad());
        participante.setRol(dto.getRol());
        participante.setGradoEstudio(dto.getGradoEstudio());
        participante.setCapituloPmi(dto.getCapituloPmi());
        participante.setIdMiembroPmi(dto.getIdMiembroPmi());
        participante.setCuentaConCertificadoPmi(dto.getCuentaConCertificadoPmi() != null ? dto.getCuentaConCertificadoPmi() : false);

        // Convertir Base64 a byte[]
        if (dto.getEvidenciaEstudio() != null && !dto.getEvidenciaEstudio().isEmpty()) {
            participante.setEvidenciaEstudio(Base64.getDecoder().decode(dto.getEvidenciaEstudio()));
        }

        // No mapear asistencias aquí para evitar ciclos al crear

        return participante;
    }

    public static List<ParticipanteDto> mapearaListaDto(List<Participante> participantes) {
        if (participantes == null) return null;
        return participantes.stream()
                .map(ParticipanteMapper::mapearaDto)
                .collect(Collectors.toList());
    }
}