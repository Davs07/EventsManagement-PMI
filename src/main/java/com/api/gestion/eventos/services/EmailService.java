package com.api.gestion.eventos.services;

import com.api.gestion.eventos.config.CalendarUtil;
import com.api.gestion.eventos.dtos.EnvioRecordatoriosResponse;
import com.api.gestion.eventos.entities.Asistencia;
import com.api.gestion.eventos.entities.Evento;
import com.api.gestion.eventos.entities.InvitacionPresencial;
import com.api.gestion.eventos.entities.InvitacionVirtual;
import com.api.gestion.eventos.entities.Participante;
import com.api.gestion.eventos.repositories.AsistenciaRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private AsistenciaRepository asistenciaRepository;

    @Autowired
    private QrCodeService qrCodeService;

    /**
     * Envía recordatorios a todos los participantes y devuelve un resumen con totales y errores.
     * Ahora recibe el flyerFile como MultipartFile (subido desde Postman) y lo lo incrusta inline en el correo.
     */
    public EnvioRecordatoriosResponse enviarRecordatorio(String asunto, String mensaje, MultipartFile flyerFile,
                                                         String resumenEvento, String descripcionEvento,
                                                         ZonedDateTime inicio, ZonedDateTime fin, String lugar,
                                                         Evento evento) {
        List<Asistencia> asistenciasEvento = asistenciaRepository.findByEvento(evento);
        String icsPath;
        try {
            icsPath = CalendarUtil.crearArchivoICS(resumenEvento, descripcionEvento, inicio, fin, lugar);
        } catch (Exception e) {
            logger.error("Error generando ICS: {}", e.getMessage(), e);
            icsPath = null;
        }

        File icsFile = (icsPath != null) ? new File(icsPath) : null;

        int total = asistenciasEvento.size();
        int enviados = 0;
        int fallidos = 0;
        List<String> errores = new ArrayList<>();

        for (Asistencia asistencia : asistenciasEvento) {
            Participante partici = asistencia.getParticipante();
            String targetEmail = partici != null ? partici.getEmail() : null;
            String nombreParticipante = partici != null ? partici.getNombres() : null;

            if (targetEmail == null || targetEmail.isBlank()) {
                String idLabel = formatParticipante(partici);
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

                // Construir HTML del correo; si hay flyer, incrustarlo como imagen inline usando CID
                StringBuilder html = new StringBuilder();
                String flyerCid = null;
                if (flyerFile != null && !flyerFile.isEmpty()) {
                    flyerCid = "flyer" + System.currentTimeMillis();
                    html.append("<p><img src=\"cid:").append(flyerCid).append("\" style=\"max-width:80vw;height:auto;\"/></p>");
                }
                html.append("<html><body>");
                html.append("<p>").append(cuerpo.replace("\n", "<br/>")).append("</p>");
                html.append("<p><strong>Evento:</strong> ").append(resumenEvento != null ? resumenEvento : "").append("</p>");
                html.append("<p><strong>Fecha:</strong> ").append(inicio != null ? inicio.toString() : "").append(" - ").append(fin != null ? fin.toString() : "").append("</p>");
                html.append("<p><strong>Lugar:</strong> ").append(lugar != null ? lugar : "").append("</p>");


                html.append("</body></html>");
                helper.setText(html.toString(), true);

                // Si hay flyer, añadir como recurso inline (si es imagen) o como adjunto si no lo es
                if (flyerFile != null && !flyerFile.isEmpty()) {
                    try {
                        byte[] bytes = flyerFile.getBytes();
                        if (bytes.length > 0) {
                            String contentType = flyerFile.getContentType() != null ? flyerFile.getContentType() : "application/octet-stream";
                            String originalName = (flyerFile.getOriginalFilename() != null && !flyerFile.getOriginalFilename().isBlank())
                                    ? flyerFile.getOriginalFilename()
                                    : "flyer";

                            if (contentType.startsWith("image/")) {
                                if (flyerCid == null) flyerCid = "flyer" + System.currentTimeMillis();
                                // insertar inline
                                helper.addInline(flyerCid, new ByteArrayResource(bytes), contentType);
                            } else {
                                // adjuntar como archivo si no es imagen (ej: PDF)
                                helper.addAttachment(originalName, new ByteArrayResource(bytes), contentType);
                            }
                        }
                    } catch (IOException | MessagingException e) {
                        logger.error("Error añadiendo flyer para {}: {}", formatParticipante(partici), e.getMessage(), e);
                    }
                }

                // Adjuntar .ics si se generó
                if (icsFile != null && icsFile.exists()) {
                    helper.addAttachment(icsFile.getName(), new FileSystemResource(icsFile));
                }

                mailSender.send(mimeMessage);
                enviados++;
            } catch (Exception e) {
                // Registrar la excepción en logs, pero sólo añadir el participante en la lista de errores
                String idLabel = formatParticipante(partici);
                logger.error("Error enviando correo a {}: {}", idLabel, e.getMessage(), e);
                errores.add(idLabel);
                fallidos++;
            }
        }

        return new EnvioRecordatoriosResponse(total, enviados, fallidos, errores);
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

    public void sendInvitacionVirtual(InvitacionVirtual invitacion, Evento evento, MultipartFile flyer) throws Exception {
        List<Asistencia> asistenciasEvento = asistenciaRepository.findByEvento(evento);

        String icsPath = CalendarUtil.crearArchivoICS(
                invitacion.getAsunto(),
                invitacion.getMensaje(),
                invitacion.getInicio(),
                invitacion.getFin(),
                invitacion.getLugar()
        );
        File icsFile = (icsPath != null) ? new File(icsPath) : null;

        for (Asistencia asistencia : asistenciasEvento) {
            try {
                MimeMessage mimeMessage = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                Participante partici = asistencia.getParticipante();
                helper.setTo(partici.getEmail());
                helper.setSubject(invitacion.getAsunto());

                // Construir HTML del mensaje e incluir link de reunión
                StringBuilder html = new StringBuilder();
                String flyerCid = null;
                if (flyer != null && !flyer.isEmpty()) {
                    flyerCid = "flyer" + System.currentTimeMillis();
                    html.append("<p><img src=\"cid:").append(flyerCid).append("\" style=\"max-width:40%;height:auto;\"/></p>");
                }
                html.append("<html><body>");
                String mensajePersonal = invitacion.getMensaje() != null ? invitacion.getMensaje().replace("{nombre}", partici.getNombres()) : "";
                html.append("<p>").append(mensajePersonal.replace("\n", "<br/>")).append("</p>");
                html.append("<p>").append(invitacion.getGoogleMeetLink() != null ? invitacion.getGoogleMeetLink() : "").append("</p>");
                //html.append("<p><strong>Inicio:</strong> ").append(invitacion.getInicio() != null ? invitacion.getInicio().toString() : "").append("</p>");
                //html.append("<p><strong>Fin:</strong> ").append(invitacion.getFin() != null ? invitacion.getFin().toString() : "").append("</p>");
                html.append("</body></html>");
                helper.setText(html.toString(), true);

                // Manejar flyer: inline para imágenes, adjunto para otros tipos
                if (flyer != null && !flyer.isEmpty()) {
                    try {
                        byte[] bytes = flyer.getBytes();
                        if (bytes.length > 0) {
                            String contentType = flyer.getContentType() != null ? flyer.getContentType() : "application/octet-stream";
                            String originalName = (flyer.getOriginalFilename() != null && !flyer.getOriginalFilename().isBlank()) ? flyer.getOriginalFilename() : "flyer";
                            if (contentType.startsWith("image/")) {
                                helper.addInline(flyerCid, new ByteArrayResource(bytes), contentType);
                            } else {
                                helper.addAttachment(originalName, new ByteArrayResource(bytes), contentType);
                            }
                        }
                    } catch (IOException | MessagingException e) {
                        logger.error("Error añadiendo flyer a invitación virtual para {}: {}", formatParticipante(partici), e.getMessage(), e);
                    }
                }

                // Adjuntar ICS si existe
                if (icsFile != null && icsFile.exists()) {
                    helper.addAttachment(icsFile.getName(), new FileSystemResource(icsFile));
                }

                mailSender.send(mimeMessage);
            } catch (Exception e) {
                logger.error("Error enviando invitación virtual a {}: {}",
                        formatParticipante(asistencia.getParticipante()), e.getMessage(), e);
            }
        }
    }

    // Sobrecarga: enviar invitación presencial para un evento concreto (usa asistencias)
    public void sendInvitacionPresencial(InvitacionPresencial invitacion, Evento evento, MultipartFile flyer) throws Exception {
        List<Asistencia> asistenciasEvento = asistenciaRepository.findByEvento(evento);
        String icsPath = CalendarUtil.crearArchivoICS(
                invitacion.getAsunto(),
                invitacion.getMensaje(),
                invitacion.getInicio(),
                invitacion.getFin(),
                invitacion.getLugar()
        );
        File icsFile = (icsPath != null) ? new File(icsPath) : null;

        for (Asistencia asistencia : asistenciasEvento) {
            try {
                Participante partici = asistencia.getParticipante();
                MimeMessage mimeMessage = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                helper.setTo(partici.getEmail());
                helper.setSubject(invitacion.getAsunto());

                // Construir HTML para la invitación presencial
                StringBuilder html = new StringBuilder();
                String flyerCid = null;
                if (flyer != null && !flyer.isEmpty()) {
                    flyerCid = "flyer" + System.currentTimeMillis();
                    html.append("<p><img src=\"cid:").append(flyerCid).append("\" style=\"max-width:40%;height:auto;\"/></p>");
                }
                html.append("<html><body>");
                String mensajePersonal = invitacion.getMensaje() != null ? invitacion.getMensaje().replace("{nombre}", partici.getNombres()) : "";
                html.append("<p>").append(mensajePersonal.replace("\n", "<br/>")).append("</p>");
                html.append("<p><strong>Lugar:</strong> ").append(invitacion.getLugar() != null ? invitacion.getLugar() : "").append("</p>");
                html.append("<p><strong>Inicio:</strong> ").append(invitacion.getInicio() != null ? invitacion.getInicio().toString() : "").append("</p>");
                html.append("<p><strong>Fin:</strong> ").append(invitacion.getFin() != null ? invitacion.getFin().toString() : "").append("</p>");
                html.append("</body></html>");
                helper.setText(html.toString(), true);

                // Manejar flyer: inline para imágenes, adjunto para otros tipos
                if (flyer != null && !flyer.isEmpty()) {
                    try {
                        byte[] bytes = flyer.getBytes();
                        if (bytes.length > 0) {
                            String contentType = flyer.getContentType() != null ? flyer.getContentType() : "application/octet-stream";
                            String originalName = (flyer.getOriginalFilename() != null && !flyer.getOriginalFilename().isBlank()) ? flyer.getOriginalFilename() : "flyer";
                            if (contentType.startsWith("image/")) {
                                helper.addInline(flyerCid, new ByteArrayResource(bytes), contentType);
                            } else {
                                helper.addAttachment(originalName, new ByteArrayResource(bytes), contentType);
                            }
                        }
                    } catch (IOException | MessagingException e) {
                        logger.error("Error añadiendo flyer a invitación presencial para {}: {}", formatParticipante(partici), e.getMessage(), e);
                    }
                }

                // Generar QR único por participante y adjuntarlo (mantener comportamiento existente)
                try {
                    String attachName = "qr_" + (partici.getId() != null ? partici.getId() : System.currentTimeMillis()) + ".png";
                    String qrContent = (asistencia.getCodigoQr() != null && !asistencia.getCodigoQr().isBlank())
                            ? asistencia.getCodigoQr()
                            : String.format("ASISTENCIA|ID:%s|EMAIL:%s|EVENTO:%s", (partici.getId() != null ? partici.getId() : "0"), partici.getEmail(), invitacion.getAsunto());

                    String qrPath = null;
                    try {
                        qrPath = qrCodeService.saveQRCodeImageToFile(qrContent, 250, 250, attachName);
                    } catch (Exception eSave) {
                        logger.warn("qrCodeService.saveQRCodeImageToFile falló: {}", eSave.getMessage());
                    }

                    if (qrPath != null) {
                        File qrFile = new File(qrPath);
                        if (qrFile.exists()) {
                            helper.addAttachment(qrFile.getName(), new FileSystemResource(qrFile));
                        } else {
                            byte[] qrBytes = qrCodeService.generateQRCodeImage(qrContent, 250, 250);
                            if (qrBytes != null && qrBytes.length > 0) {
                                helper.addAttachment(attachName, new ByteArrayResource(qrBytes));
                            }
                        }
                    } else {
                        byte[] qrBytes = qrCodeService.generateQRCodeImage(qrContent, 250, 250);
                        if (qrBytes != null && qrBytes.length > 0) {
                            helper.addAttachment(attachName, new ByteArrayResource(qrBytes));
                        }
                    }
                } catch (Exception e) {
                    logger.error("Error generando/adjuntando QR para {}: {}", formatParticipante(asistencia.getParticipante()), e.getMessage(), e);
                }

                // Adjuntar ICS si existe
                if (icsFile != null && icsFile.exists()) {
                    helper.addAttachment(icsFile.getName(), new FileSystemResource(icsFile));
                }

                mailSender.send(mimeMessage);
            } catch (Exception e) {
                logger.error("Error enviando invitación presencial a {}: {}", (asistencia.getParticipante() != null ? asistencia.getParticipante().getEmail() : "?"), e.getMessage(), e);
            }
  }
}
    public void enviarCertificado(String destinatario, String nombre,
                                  byte[] pdfBytes, String nombreEvento, String mensaje)
            throws MessagingException {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom("pminorteperu1@gmail.com");
        helper.setTo(destinatario);
        helper.setSubject("🎓 Certificado de Participación - " + nombreEvento);

        String contenido = String.format("""
        <h2>¡Felicitaciones %s!</h2>
        <p>Adjunto encontrarás tu certificado de participación en <strong>%s</strong>.</p>
        <p>%s</p>
        <p>Tu código de certificado te permitirá verificar su autenticidad.</p>
        """, nombre, nombreEvento, mensaje != null ? mensaje : "");

        helper.setText(contenido, true);
        helper.addAttachment("Certificado.pdf", new ByteArrayResource(pdfBytes));

        mailSender.send(message);
    }

}