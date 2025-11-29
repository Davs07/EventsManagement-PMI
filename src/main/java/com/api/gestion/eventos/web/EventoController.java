package com.api.gestion.eventos.web;

import com.api.gestion.eventos.dtos.EventoDTO;
import com.api.gestion.eventos.entities.Evento;
import com.api.gestion.eventos.enums.EstadoEvento;
import com.api.gestion.eventos.enums.TipoEvento;
import com.api.gestion.eventos.mappers.EventoMapper;
import com.api.gestion.eventos.repositories.EventoRepository;
import com.api.gestion.eventos.services.EventoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/eventos")
public class EventoController {
    @Autowired
    private EventoService eventoService;
    @Autowired
    private EventoRepository eventoRepository;

    @PostMapping("/crear")
    public ResponseEntity<EventoDTO> crear(@RequestBody EventoDTO eventoDTO) {
        Evento evento = new Evento();
        evento.setNombre(eventoDTO.getNombre());
        evento.setDescripcion(eventoDTO.getDescripcion());
        evento.setFechaInicio(eventoDTO.getFechaInicio());
        evento.setFechaFin(eventoDTO.getFechaFin());

        // Mapear tipo de evento
        if (eventoDTO.getTipoEvento() != null) {
            evento.setTipoEvento(TipoEvento.valueOf(eventoDTO.getTipoEvento()));
        }

        evento.setUbicacion(eventoDTO.getUbicacion());
        evento.setCapacidadMaxima(eventoDTO.getCapacidadMaxima());
        evento.setBrindaCertificado(eventoDTO.getBrindaCertificado() != null ? eventoDTO.getBrindaCertificado() : true);

        // Mapear estado del evento
        if (eventoDTO.getEstadoEvento() != null) {
            evento.setEstadoEvento(EstadoEvento.valueOf(eventoDTO.getEstadoEvento()));
        }

        // Convertir Base64 de vuelta a byte[]
        if (eventoDTO.getPlantillaImagen() != null && !eventoDTO.getPlantillaImagen().isEmpty()) {
            try {
                byte[] imageBytes = Base64.getDecoder().decode(eventoDTO.getPlantillaImagen());
                evento.setPlantillaImagen(imageBytes);
            } catch (IllegalArgumentException e) {
                // Si no es Base64 válido, ignorar
                evento.setPlantillaImagen(null);
            }
        }

        // Mapear evento padre si existe
        if (eventoDTO.getEventoPadreId() != null) {
            Evento eventoPadre = eventoService.obtenerEventoPorId(eventoDTO.getEventoPadreId());
            evento.setEventoPadre(eventoPadre);
        }

        Evento creado = eventoService.crearEvento(evento);
        return ResponseEntity.ok(EventoMapper.toDto(creado));
    }

    @GetMapping("/listar")
    public ResponseEntity<List<EventoDTO>> listar() {
        List<Evento> lista = eventoRepository.findAll();
        List<EventoDTO> dtos = EventoMapper.toDtoList(lista);
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventoDTO> obtenerPorId(@PathVariable Long id) {
        try {
            Evento evento = eventoService.obtenerEventoPorId(id);
            return ResponseEntity.ok(EventoMapper.toDto(evento));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventoDTO> actualizar(@PathVariable Long id, @RequestBody EventoDTO eventoDTO) {
        try {
            Evento evento = eventoService.obtenerEventoPorId(id);
            evento.setNombre(eventoDTO.getNombre());
            evento.setDescripcion(eventoDTO.getDescripcion());
            // ... otros campos

            // Actualizar imagen si viene en el DTO
            if (eventoDTO.getPlantillaImagen() != null && !eventoDTO.getPlantillaImagen().isEmpty()) {
                try {
                    byte[] imageBytes = Base64.getDecoder().decode(eventoDTO.getPlantillaImagen());
                    evento.setPlantillaImagen(imageBytes);
                } catch (IllegalArgumentException e) {
                    // Mantener imagen anterior si no es Base64 válido
                }
            }

            Evento actualizado = eventoService.actualizarEvento(id, evento);
            return ResponseEntity.ok(EventoMapper.toDto(actualizado));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        try {
            eventoService.eliminarEvento(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    // ⭐ NUEVO: Endpoint para subir imagen por separado (opcional)
    @PostMapping("/{id}/imagen")
    public ResponseEntity<EventoDTO> subirImagen(
            @PathVariable Long id,
            @RequestParam("imagen") MultipartFile imagen) {
        try {
            Evento evento = eventoService.obtenerEventoPorId(id);
            evento.setPlantillaImagen(imagen.getBytes());
            Evento actualizado = eventoRepository.save(evento);
            return ResponseEntity.ok(EventoMapper.toDto(actualizado));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    //Metodos para los certificados de los eventos
    @PostMapping("/{eventoId}/plantilla-certificado")
    public ResponseEntity<?> subirPlantillaCertificado(
            @PathVariable Long eventoId,
            @RequestParam("plantilla") MultipartFile plantilla) {

        try {
            if (!plantilla.getContentType().equals("application/pdf")) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Solo se permiten archivos PDF"));
            }

            if (plantilla.getSize() > 10 * 1024 * 1024) { // 5MB máximo
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "El archivo no debe superar 5MB"));
            }

            Evento evento = eventoService.obtenerEventoPorId(eventoId);
            evento.setPlantillaCertificado(plantilla.getBytes());
            eventoService.actualizarEvento(eventoId, evento);

            return ResponseEntity.ok(Map.of(
                    "mensaje", "Plantilla de certificado subida correctamente",
                    "eventoId", eventoId
            ));

        } catch (IOException e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Error al procesar el archivo: " + e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404)
                    .body(Map.of("error", "Evento no encontrado"));
        }
    }

    @GetMapping("/{eventoId}/plantilla-certificado")
    public ResponseEntity<Resource> obtenerPlantillaCertificado(@PathVariable Long eventoId) {
        try {
            Evento evento = eventoService.obtenerEventoPorId(eventoId);

            if (evento.getPlantillaCertificado() == null) {
                return ResponseEntity.notFound().build();
            }

            ByteArrayResource resource = new ByteArrayResource(evento.getPlantillaCertificado());

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"plantilla_certificado_evento_" + eventoId + ".pdf\"")
                    .body(resource);

        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}