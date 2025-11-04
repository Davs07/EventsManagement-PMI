package com.api.gestion.eventos.web;

import com.api.gestion.eventos.dtos.EnvioRecordatoriosResponse;
import com.api.gestion.eventos.entities.InvitacionPresencial;
import com.api.gestion.eventos.entities.InvitacionVirtual;
import com.api.gestion.eventos.repositories.EventoRepository;
import com.api.gestion.eventos.services.EmailService;
import com.api.gestion.eventos.services.InvitacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.ZonedDateTime;
import java.util.Collections;

@RestController
@RequestMapping("/api/email")
@CrossOrigin(origins = "*") // Ajustar según tu configuración CORS
public class EmailController {
    @Autowired
    private EmailService emailService;
    @Autowired
    private InvitacionService invitacionService;
    @Autowired
    private EventoRepository eventoRepository;

    @PostMapping(value = "/recordatorio", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EnvioRecordatoriosResponse> enviarRecordatorio(
            @RequestParam Long eventoId,
            @RequestParam String asunto,
            @RequestParam String mensaje,
            @RequestParam String resumenEvento,
            @RequestParam String descripcionEvento,
            @RequestParam String inicio,
            @RequestParam String fin,
            @RequestParam String lugar,
            @RequestPart(value = "flyer", required = false) MultipartFile flyer) {
        try {
            EnvioRecordatoriosResponse resp = emailService.enviarRecordatorio(
                    asunto,
                    mensaje,
                    flyer,
                    resumenEvento,
                    descripcionEvento,
                    ZonedDateTime.parse(inicio),
                    ZonedDateTime.parse(fin),
                    lugar,
                    eventoRepository.findById(eventoId).orElse(null)
            );
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            EnvioRecordatoriosResponse errorResp = new EnvioRecordatoriosResponse(0, 0, 0,
                    Collections.singletonList("Error: " + e.getMessage()));
            return ResponseEntity.internalServerError().body(errorResp);
        }
    }

    @PostMapping(value = "/virtual", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> enviarInvitacionVirtual(
            @RequestParam Long eventoId,
            @RequestParam String asunto,
            @RequestParam String mensaje,
            @RequestParam String googleMeetLink,
            @RequestParam String inicio,
            @RequestParam String fin,
            @RequestParam String lugar,
            @RequestPart(value = "flyer", required = false) MultipartFile flyer) {
        try {
            InvitacionVirtual invitacion = new InvitacionVirtual();
            invitacion.setAsunto(asunto);
            invitacion.setMensaje(mensaje);
            invitacion.setGoogleMeetLink(googleMeetLink);
            invitacion.setInicio(ZonedDateTime.parse(inicio));
            invitacion.setFin(ZonedDateTime.parse(fin));
            invitacion.setLugar(lugar);

            invitacionService.enviarInvitacionesVirtuales(
                    invitacion,
                    eventoRepository.findById(eventoId).orElse(null),
                    flyer);
            return ResponseEntity.ok("Invitaciones virtuales enviadas exitosamente");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al enviar invitaciones: " + e.getMessage());
        }
    }

    @PostMapping(value = "/presencial", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> enviarInvitacionPresencial(
            @RequestParam Long eventoId,
            @RequestParam String asunto,
            @RequestParam String mensaje,
            //@RequestParam String qrCode,
            @RequestParam String inicio,
            @RequestParam String fin,
            @RequestParam String lugar,
            @RequestPart(value = "flyer", required = false) MultipartFile flyer) {
        try {
            InvitacionPresencial invitacion = new InvitacionPresencial();
            invitacion.setAsunto(asunto);
            invitacion.setMensaje(mensaje);
            //invitacion.setQrCode(qrCode);
            invitacion.setInicio(ZonedDateTime.parse(inicio));
            invitacion.setFin(ZonedDateTime.parse(fin));
            invitacion.setLugar(lugar);

            invitacionService.enviarInvitacionesPresenciales(
                    invitacion,
                    eventoRepository.findById(eventoId).orElse(null),
                    flyer);

            return ResponseEntity.ok("Invitaciones presenciales enviadas exitosamente");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al enviar invitaciones: " + e.getMessage());
 }
}
}