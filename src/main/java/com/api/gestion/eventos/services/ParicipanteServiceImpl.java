package com.api.gestion.eventos.services;

import com.api.gestion.eventos.dtos.ParticipanteDto;
import com.api.gestion.eventos.entities.Participante;
import com.api.gestion.eventos.mappers.ParticipanteMapper;
import com.api.gestion.eventos.repositories.ParticipanteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ParicipanteServiceImpl implements ParticipanteService {

    @Autowired
    private ParticipanteRepository participanteRepository;

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
        return null;
    }

    @Override
    public ParticipanteDto actualizar(ParticipanteDto participanteDto) {
        return null;
    }

    @Override
    public void eliminarporId(Long id) {

    }
}
