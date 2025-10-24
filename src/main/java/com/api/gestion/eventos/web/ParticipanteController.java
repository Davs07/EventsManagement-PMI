package com.api.gestion.eventos.web;

import com.api.gestion.eventos.dtos.ParticipanteDto;
import com.api.gestion.eventos.services.ParticipanteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/participante")
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


}
