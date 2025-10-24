package com.api.gestion.eventos.services;

import com.api.gestion.eventos.entities.Evento;

public interface EventoService {
    Evento crearEvento(Evento evento);
    Evento obtenerEventoPorId(Long id)throws RuntimeException;
    Evento actualizarEvento(Long id, Evento evento);
    void eliminarEvento(Long id);
}