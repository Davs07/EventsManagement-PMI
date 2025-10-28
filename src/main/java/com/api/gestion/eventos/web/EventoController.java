package com.api.gestion.eventos.web;

import com.api.gestion.eventos.dtos.EventoDTO;
import com.api.gestion.eventos.entities.Evento;
import com.api.gestion.eventos.mappers.EventoMapper;
import com.api.gestion.eventos.repositories.EventoRepository;
import com.api.gestion.eventos.services.EventoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/eventos")
@CrossOrigin(origins = "*") // Ajustar según tu configuración CORS
public class EventoController {
    @Autowired
    private EventoService eventoService;
    @Autowired
    private EventoRepository eventoRepository;

    @PostMapping("/crear")
    public Evento crear(@RequestBody Evento evento) {
        Evento creado = eventoService.crearEvento(evento);
        return creado;
    }

    // Cambio: devolver DTO sin la colección de asistencias para evitar referencias circulares
    @GetMapping("/listar")
    public ResponseEntity<List<EventoDTO>> listar() {
        List<Evento> lista = eventoRepository.findAll();
        List<EventoDTO> dtos = EventoMapper.toDtoList(lista);
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Evento> obtenerPorId(@PathVariable Long id) {
        try {
            Evento evento = eventoService.obtenerEventoPorId(id);
            return ResponseEntity.ok(evento);
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Evento> actualizar(@PathVariable Long id, @RequestBody Evento evento) {
        try {
            evento.setId(id);
            Evento actualizado = eventoService.actualizarEvento(id, evento);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        try {
            eventoService.eliminarEvento(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}