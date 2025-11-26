package com.api.gestion.eventos.web;

import com.api.gestion.eventos.dtos.ParticipanteDto;
import com.api.gestion.eventos.entities.ParticipanteConAsistenciaDTO;
import com.api.gestion.eventos.services.ParticipanteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/participante")
@CrossOrigin(origins = "*") // Ajustar según tu configuración CORS
public class ParticipanteController {
    @Autowired
    private ParticipanteService participanteService;
    
    //ACTUALIZAR DTO
    @PostMapping ("/crear")
    public ResponseEntity<?> guardar (@RequestBody ParticipanteDto participanteDto){
        try {
            ParticipanteDto creado = participanteService.crear(participanteDto);
            return ResponseEntity.ok(creado);
        } catch (Exception e) {
            e.printStackTrace(); // Para ver el error en consola
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear participante: " + e.getMessage());
        }
    }
    //ACTUALIZAR DTO
    @GetMapping ("/listar")
    public List<ParticipanteDto> listar(){
        return participanteService.listar();
    }

    @DeleteMapping ("/eliminar/{id}")
    public void eliminar (@PathVariable Long id){
        participanteService.eliminarporId(id);
    }

    @GetMapping("/evento/{eventoId}")
    public ResponseEntity<List<ParticipanteDto>> listarPorEvento(@PathVariable Long eventoId) {
        List<ParticipanteDto> participantes = participanteService.listarPorEvento(eventoId);
        return ResponseEntity.ok(participantes);
    }
    @GetMapping("/evento/{eventoId}/con-asistencia")
    public ResponseEntity<List<ParticipanteConAsistenciaDTO>> getParticipantesConAsistencia(
            @PathVariable Long eventoId) {
        List<ParticipanteConAsistenciaDTO> participantes =
                participanteService.getParticipantesConAsistencia(eventoId);
        return ResponseEntity.ok(participantes);
    }
}
