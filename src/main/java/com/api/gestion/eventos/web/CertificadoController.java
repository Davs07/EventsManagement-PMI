package com.api.gestion.eventos.web;

import com.api.gestion.eventos.services.Certificados.CertificadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/certificados")
@CrossOrigin("http://localhost:3000/")
public class CertificadoController {

    @Autowired
    private CertificadoService certificadoService;

    @PostMapping("/evento/{eventoId}/enviar")
    public ResponseEntity<String> enviarCertificados(@PathVariable Long eventoId, @RequestParam String mensaje) {
        try {
            certificadoService.enviarCertificadosEvento(eventoId, mensaje);
            return ResponseEntity.ok("Certificados enviados correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/validar/{codigo}")
    public ResponseEntity<Boolean> validarCertificado(@PathVariable String codigo) {
        boolean valido = certificadoService.validarCertificado(codigo);
        return ResponseEntity.ok(valido);
    }
}