package com.api.gestion.eventos.web;

import com.api.gestion.eventos.dtos.ParticipanteDto;
import com.api.gestion.eventos.services.ParticipanteService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ParticipanteDto guardar (@RequestBody ParticipanteDto participanteDto){
        return participanteService.crear(participanteDto);
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
}
