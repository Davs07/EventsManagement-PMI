package com.api.gestion.eventos.web;

import com.api.gestion.eventos.dtos.AsistenciaDTO;
import com.api.gestion.eventos.entities.Asistencia;
import com.api.gestion.eventos.mappers.AsistenciaMapper;
import com.api.gestion.eventos.services.AsistenciaService;
import com.api.gestion.eventos.services.QrCodeService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/asistencias")
@Slf4j
public class AsistenciaController {

    @Autowired
    private AsistenciaService asistenciaService;
    @Autowired
    private QrCodeService qrCodeService;

    @PostMapping("/crear")
    public ResponseEntity<?> crearAsistencia(@RequestBody AsistenciaDTO dto) {
        log.info("Crear Asistencia DTO recibido: {}", dto);
        System.out.println("Crear Asistencia DTO recibido: " + dto);
        
        if (dto.getParticipanteId() == null || dto.getEventoId() == null) {
            return ResponseEntity.badRequest().body("ParticipanteId y EventoId son requeridos");
        }
        
        try {
            Asistencia entidad = AsistenciaMapper.toEntity(dto);
            Asistencia creado = asistenciaService.crearAsistencia(entidad);
            AsistenciaDTO resp = AsistenciaMapper.toDto(creado);
            var location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                    .buildAndExpand(creado.getId()).toUri();
            return ResponseEntity.created(location).body(resp);
        } catch (IllegalStateException e) {
            // Error de duplicado
            log.warn("Error al crear asistencia: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (RuntimeException e) {
            // Otros errores
            log.error("Error al crear asistencia: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<AsistenciaDTO> obtenerAsistencia(@PathVariable Long id) {
        Asistencia a = asistenciaService.obtenerAsistenciaPorId(id);
        return ResponseEntity.ok(AsistenciaMapper.toDto(a));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<AsistenciaDTO>> obtenerPorUsuario(@PathVariable Long usuarioId) {
        List<AsistenciaDTO> lista = AsistenciaMapper.toDtoList(asistenciaService.obtenerAsistenciasPorUsuario(usuarioId));
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/evento/{eventoId}")
    public ResponseEntity<List<AsistenciaDTO>> obtenerPorEvento(@PathVariable Long eventoId) {
        List<AsistenciaDTO> lista = AsistenciaMapper.toDtoList(asistenciaService.obtenerAsistenciasPorEvento(eventoId));
        return ResponseEntity.ok(lista);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AsistenciaDTO> actualizarAsistencia(@PathVariable Long id, @RequestBody AsistenciaDTO dto) {
        Asistencia entidad = AsistenciaMapper.toEntity(dto);
        entidad.setId(id);
        Asistencia actualizado = asistenciaService.actualizarAsistencia(id, entidad);
        return ResponseEntity.ok(AsistenciaMapper.toDto(actualizado));
    }

    @PutMapping("/{id}/registrar")
    public ResponseEntity<AsistenciaDTO> registrarAsistencia(@PathVariable Long id) {
        Asistencia registrado = asistenciaService.registrarAsistencia(id);
        return ResponseEntity.ok(AsistenciaMapper.toDto(registrado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarAsistencia(@PathVariable Long id) {
        asistenciaService.eliminarAsistencia(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/registrar-qr")
    public ResponseEntity<?> registrarAsistenciaPorQR(@RequestBody Map<String, String> request) {
        try {
            String codigoQr = request.get("codigoQr");
            if (codigoQr == null || codigoQr.isEmpty()) {
                return ResponseEntity.badRequest().body("El código QR es requerido");
            }
            Asistencia asistencia = asistenciaService.registrarAsistenciaPorQR(codigoQr);
            return ResponseEntity.ok(AsistenciaMapper.toDto(asistencia));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/{id}/qrcode")
    public ResponseEntity<byte[]> obtenerQRCode(@PathVariable Long id) {
        try {
            byte[] qrCode = asistenciaService.generarQRParaAsistencia(id);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            return new ResponseEntity<>(qrCode, headers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/validar-qr-imagen")
    public ResponseEntity<?> validarQrDesdeImagen(@RequestParam("imagen") MultipartFile imagen) {
        try {
            String codigoQr = qrCodeService.decodeQRCode(imagen.getBytes());
            Asistencia asistencia = asistenciaService.registrarAsistenciaPorQR(codigoQr);
            return ResponseEntity.ok(AsistenciaMapper.toDto(asistencia));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al procesar la imagen QR: " + e.getMessage());
        }
    }

    // Obtener asistencia específica por participante y evento
    @GetMapping("/participante/{participanteId}/evento/{eventoId}")
    public ResponseEntity<AsistenciaDTO> obtenerAsistenciaPorParticipanteYEvento(
            @PathVariable Long participanteId,
            @PathVariable Long eventoId) {
        try {
            Asistencia asistencia = asistenciaService.obtenerAsistenciaPorParticipanteYEvento(participanteId, eventoId);
            return ResponseEntity.ok(AsistenciaMapper.toDto(asistencia));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Actualizar solo el estado de asistencia
    @PatchMapping("/participante/{participanteId}/evento/{eventoId}/estado")
    public ResponseEntity<AsistenciaDTO> actualizarEstadoAsistencia(
            @PathVariable Long participanteId,
            @PathVariable Long eventoId,
            @RequestBody Map<String, Boolean> request) {
        try {
            Boolean asistio = request.get("asistio");
            if (asistio == null) {
                return ResponseEntity.badRequest().build();
            }
            Asistencia actualizada = asistenciaService.actualizarEstadoAsistencia(participanteId, eventoId, asistio);
            return ResponseEntity.ok(AsistenciaMapper.toDto(actualizada));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}