package com.api.gestion.eventos.services.Impl;

import com.api.gestion.eventos.entities.Evento;
import com.api.gestion.eventos.repositories.EventoRepository;
import com.api.gestion.eventos.services.EventoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EventoServiceImpl implements EventoService {

    @Autowired
    private EventoRepository eventoRepository;

    @Override
    public Evento crearEvento(Evento evento) {
        return eventoRepository.save(evento);
    }

    @Override
    public Evento obtenerEventoPorId(Long id) throws RuntimeException {
        return eventoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado con id: " + id));
    }

    @Override
    public Evento actualizarEvento(Long id, Evento evento) {
        evento.setId(id);
        return eventoRepository.save(evento);
    }

    @Override
    public void eliminarEvento(Long id) {
        eventoRepository.deleteById(id);
    }
}
