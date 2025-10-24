package com.api.gestion.eventos.web;

import com.api.gestion.eventos.dtos.EnvioRecordatoriosResponse;
import com.api.gestion.eventos.services.EmailService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.Collections;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    private static final String UPLOAD_DIR = "uploads/flyers/";

    @PostMapping(value = "/recordatorio", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EnvioRecordatoriosResponse> enviarRecordatorio(
            @RequestParam("asunto") String asunto,
            @RequestParam("mensaje") String mensaje,
            @RequestParam(value = "flyer", required = false) MultipartFile flyer,
            @RequestParam("resumenEvento") String resumenEvento,
            @RequestParam("descripcionEvento") String descripcionEvento,
            @RequestParam("inicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime inicio,
            @RequestParam("fin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime fin,
            @RequestParam("lugar") String lugar
    ) {
        try {
            String flyerPath = null;

            if (flyer != null && !flyer.isEmpty()) {
                try {
                    flyerPath = guardarArchivo(flyer);
                } catch (IOException e) {
                    EnvioRecordatoriosResponse errorResp = new EnvioRecordatoriosResponse(0, 0, 0,
                            Collections.singletonList("Error guardando archivo: " + e.getMessage()));
                    return ResponseEntity.internalServerError().body(errorResp);
                }
            }

            EnvioRecordatoriosResponse resp = emailService.enviarRecordatorio(
                    asunto,
                    mensaje,
                    flyerPath,
                    resumenEvento,
                    descripcionEvento,
                    inicio,
                    fin,
                    lugar
            );
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            EnvioRecordatoriosResponse errorResp = new EnvioRecordatoriosResponse(0, 0, 0,
                    Collections.singletonList("Error: " + e.getMessage()));
            return ResponseEntity.internalServerError().body(errorResp);
        }
    }

    private String guardarArchivo(MultipartFile archivo) throws IOException {
        String contentType = archivo.getContentType();
        if (contentType == null || !(contentType.equals("application/pdf") || contentType.startsWith("image/"))) {
            throw new IOException("Tipo de archivo no permitido: " + contentType);
        }

        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            boolean ok = uploadDir.mkdirs();
            if (!ok) {
                throw new IOException("No se pudo crear el directorio de uploads: " + UPLOAD_DIR);
            }
        }

        String original = archivo.getOriginalFilename();
        String safeOriginal = (original == null || original.isBlank()) ? "file" : original;
        String nombreSeguro = System.currentTimeMillis() + "_" + safeOriginal.replaceAll("\\s+", "_");
        Path rutaArchivo = Paths.get(UPLOAD_DIR, nombreSeguro);
        Files.write(rutaArchivo, archivo.getBytes());
        return rutaArchivo.toString();
    }
}