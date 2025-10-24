package com.api.gestion.eventos.services;

import com.api.gestion.eventos.config.CalendarUtil;
import com.api.gestion.eventos.dtos.EnvioRecordatoriosResponse;
import com.api.gestion.eventos.entities.Participante;
import com.api.gestion.eventos.repositories.ParticipanteRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private ParticipanteRepository participanteRepository;
    @Autowired
    private JavaMailSender mailSender;

    /**
     * Envía recordatorios a todos los participantes y devuelve un resumen con totales y errores.
     */
    public EnvioRecordatoriosResponse enviarRecordatorio(String asunto, String mensaje, String flyerPath,
                                                         String resumenEvento, String descripcionEvento,
                                                         ZonedDateTime inicio, ZonedDateTime fin, String lugar) {
        List<Participante> participantes = participanteRepository.findAll();
        String icsPath;
        try {
            icsPath = CalendarUtil.crearArchivoICS(resumenEvento, descripcionEvento, inicio, fin, lugar);
        } catch (Exception e) {
            // No detener envío por fallo al generar .ics; registrar error y seguir sin archivo .ics
            logger.error("Error generando ICS: {}", e.getMessage(), e);
            icsPath = null;
        }

        File icsFile = (icsPath != null) ? new File(icsPath) : null;
        File flyer = null;
        if (flyerPath != null && !flyerPath.isBlank()) {
            flyer = new File(flyerPath);
        }

        int total = participantes.size();
        int enviados = 0;
        int fallidos = 0;
        List<String> errores = new ArrayList<>();

        for (Participante participante : participantes) {
            String targetEmail = participante.getEmail();
            String nombreParticipante = participante.getNombres();

            if (targetEmail == null || targetEmail.isBlank()) {
                String idLabel = formatParticipante(participante);
                logger.error("Participante sin email: {}", idLabel);
                errores.add(idLabel);
                fallidos++;
                continue;
            }

            try {
                MimeMessage mimeMessage = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                helper.setTo(targetEmail);
                helper.setSubject(asunto);
                // Reemplazar marcador {nombre} en el mensaje si existe
                String cuerpo = (mensaje != null) ? mensaje.replace("{nombre}", nombreParticipante != null ? nombreParticipante : "") : "";
                helper.setText(cuerpo, true);

                // Adjuntar flyer si fue proporcionado y existe
                attachIfExists(helper, flyer);

                if (icsFile != null && icsFile.exists()) {
                    helper.addAttachment(icsFile.getName(), new FileSystemResource(icsFile));
                }

                mailSender.send(mimeMessage);
                enviados++;
            } catch (Exception e) {
                // Registrar la excepción en logs, pero sólo añadir el participante en la lista de errores
                String idLabel = formatParticipante(participante);
                logger.error("Error enviando correo a {}: {}", idLabel, e.getMessage(), e);
                errores.add(idLabel);
                fallidos++;
            }
        }

        return new EnvioRecordatoriosResponse(total, enviados, fallidos, errores);
    }

    private void attachIfExists(MimeMessageHelper helper, File file) {
        if (file != null && file.exists()) {
            try {
                helper.addAttachment(file.getName(), new FileSystemResource(file));
            } catch (MessagingException e) {
                logger.error("Error adjuntando archivo {}: {}", file.getName(), e.getMessage(), e);
            }
        }
    }

    private String formatParticipante(Participante p) {
        if (p == null) return "participante_desconocido";
        String email = (p.getEmail() == null || p.getEmail().isBlank()) ? null : p.getEmail();
        if (email != null) {
            return "id=" + p.getId() + ", email=" + email;
        }
        String nombre = (p.getNombres() == null || p.getNombres().isBlank()) ? "sin_nombre" : p.getNombres();
        return "id=" + p.getId() + ", nombre=" + nombre;
    }

}