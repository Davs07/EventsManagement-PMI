package com.api.gestion.eventos.services.Impl;

import com.api.gestion.eventos.dtos.ParticipanteDto;
import com.api.gestion.eventos.entities.Participante;
import com.api.gestion.eventos.mappers.ParticipanteMapper;
import com.api.gestion.eventos.repositories.AsistenciaRepository;
import com.api.gestion.eventos.repositories.ParticipanteRepository;
import com.api.gestion.eventos.services.ParticipanteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class ParicipanteServiceImpl implements ParticipanteService {

    @Autowired
    private ParticipanteRepository participanteRepository;
    @Autowired
    private AsistenciaRepository asistenciaRepository;

    @Override
    public ParticipanteDto crear(ParticipanteDto participanteDto) {
        Participante participante = ParticipanteMapper.mapearaEntidad(participanteDto);
        Participante participanteBD = participanteRepository.save(participante);
        ParticipanteDto participanteDto1 = ParticipanteMapper.mapearaDto(participanteBD);
        return participanteDto1;
    }

    @Override
    public List<ParticipanteDto> listar() {
        List<Participante> listaParticipante = participanteRepository.findAll();
        List<ParticipanteDto> listadeParticipanteDto = ParticipanteMapper.mapearaListaDto(listaParticipante);
        return listadeParticipanteDto;
    }

    @Override
    public ParticipanteDto buscarporId(Long id) {
        Participante participante = participanteRepository.findById(id).orElseThrow(() -> new RuntimeException("Participante no encontrado con id: " + id));
        ParticipanteDto participanteDto = ParticipanteMapper.mapearaDto(participante);
        return participanteDto;
    }

    @Override
    public ParticipanteDto actualizar(ParticipanteDto participanteDto) {
        return null;
    }

    @Override
    public void eliminarporId(Long id) {
        participanteRepository.deleteById(id);
    }

    @Override
    public List<ParticipanteDto> listarPorEvento(Long eventoId) {
        List<Participante> participantes = asistenciaRepository.findParticipantesByEventoId(eventoId);
        participantes.sort(Comparator.comparing(Participante::getApellidoPaterno));
        return ParticipanteMapper.mapearaListaDto(participantes);
    }
}
