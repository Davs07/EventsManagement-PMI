package com.api.gestion.eventos.web;

import com.api.gestion.eventos.dtos.EnvioRecordatoriosResponse;
import com.api.gestion.eventos.dtos.RecordatorioRequest;
import com.api.gestion.eventos.entities.InvitacionPresencial;
import com.api.gestion.eventos.entities.InvitacionVirtual;
import com.api.gestion.eventos.repositories.EventoRepository;
import com.api.gestion.eventos.services.EmailService;
import com.api.gestion.eventos.services.InvitacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.Collections;

@RestController
@RequestMapping("/api/email")
public class EmailController {
    @Autowired
    private EmailService emailService;
    @Autowired
    private InvitacionService invitacionService;

    @PostMapping("/recordatorio")
    public ResponseEntity<EnvioRecordatoriosResponse> enviarRecordatorio(@RequestBody RecordatorioRequest request) {
        try {
            EnvioRecordatoriosResponse resp = emailService.enviarRecordatorio(
                    request.getAsunto(),
                    request.getMensaje(),
                    request.getFlyerPath(),
                    request.getResumenEvento(),
                    request.getDescripcionEvento(),
                    request.getInicio(),
                    request.getFin(),
                    request.getLugar()
            );
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            EnvioRecordatoriosResponse errorResp = new EnvioRecordatoriosResponse(0, 0, 0,
                    Collections.singletonList("Error: " + e.getMessage()));
            return ResponseEntity.internalServerError().body(errorResp);
        }
    }

    @PostMapping("/virtual")
    public ResponseEntity<?> enviarInvitacionVirtual(@RequestBody InvitacionVirtual invitacion) {
        try {
            invitacionService.enviarInvitacionesVirtuales(invitacion);
            return ResponseEntity.ok("Invitaciones virtuales enviadas exitosamente");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al enviar invitaciones: " + e.getMessage());
        }
    }

    @Autowired
    private EventoRepository eventoRepository;

    @PostMapping("/presencial")
    public ResponseEntity<?> enviarInvitacionPresencial(@RequestBody InvitacionPresencial invitacion,
                                                         @RequestParam Long eventoId) {
        try {

            invitacionService.enviarInvitacionesPresenciales(invitacion, eventoRepository.findById(eventoId).orElse(null));
            return ResponseEntity.ok("Invitaciones presenciales enviadas exitosamente");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al enviar invitaciones: " + e.getMessage());
        }
    }
}