package com.api.gestion.eventos.services;

import com.api.gestion.eventos.config.CalendarUtil;
import com.api.gestion.eventos.dtos.EnvioRecordatoriosResponse;
import com.api.gestion.eventos.entities.Asistencia;
import com.api.gestion.eventos.entities.Evento;
import com.api.gestion.eventos.entities.InvitacionPresencial;
import com.api.gestion.eventos.entities.InvitacionVirtual;
import com.api.gestion.eventos.entities.Participante;
import com.api.gestion.eventos.repositories.AsistenciaRepository;
import com.api.gestion.eventos.repositories.ParticipanteRepository;
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

    @Autowired
    private AsistenciaRepository asistenciaRepository;

    @Autowired
    private QrCodeService qrCodeService;

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

    public void sendInvitacionVirtual(InvitacionVirtual invitacion) throws Exception {
        List<Participante> participantes = participanteRepository.findAll();
        String icsPath = CalendarUtil.crearArchivoICS(
                invitacion.getAsunto(),
                invitacion.getMensaje(),
                invitacion.getInicio(),
                invitacion.getFin(),
                invitacion.getLugar()
        );
        File icsFile = new File(icsPath);

        for (Participante partic : participantes) {
                try {
                    MimeMessage mimeMessage = mailSender.createMimeMessage();
                    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
                    helper.setTo(partic.getEmail());
                    helper.setSubject(invitacion.getAsunto());

                    // Incluir el link de Google Meet en el mensaje
                    String mensajeConLink = invitacion.getMensaje().replace("{nombre}", partic.getNombres())
                            + "\n\n🔗 Enlace de reunión: " + invitacion.getGoogleMeetLink();
                    helper.setText(mensajeConLink, true);

                    if (invitacion.getFlyerPath() != null) {
                        File flyer = new File(invitacion.getFlyerPath());
                        if (flyer.exists()) {
                            helper.addAttachment(flyer.getName(), new FileSystemResource(flyer));
                        }
                    }

                    if (icsFile.exists()) {
                        helper.addAttachment(icsFile.getName(), new FileSystemResource(icsFile));
                    }

                    mailSender.send(mimeMessage);
                } catch (Exception e) {
                    logger.error("Error enviando invitación virtual a {}: {}", partic.getEmail(), e.getMessage(), e);
                }
        }
    }


//    public void sendInvitacionPresencial(InvitacionPresencial invitacion) throws Exception {
//        List<Participante> participantes = participanteRepository.findAll();
//        String icsPath = CalendarUtil.crearArchivoICS(
//                invitacion.getAsunto(),
//                invitacion.getMensaje(),
//                invitacion.getInicio(),
//                invitacion.getFin(),
//                invitacion.getLugar()
//        );
//        File icsFile = new File(icsPath);
//
//        for (Participante partici : participantes) {
//                try {
//                    MimeMessage mimeMessage = mailSender.createMimeMessage();
//                    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
//                    helper.setTo(partici.getEmail());
//                    helper.setSubject(invitacion.getAsunto());
//                    helper.setText(invitacion.getMensaje().replace("{nombre}", partici.getNombres()), true);
//
//                    // Adjuntar flyer si existe
//                    if (invitacion.getFlyerPath() != null) {
//                        File flyer = new File(invitacion.getFlyerPath());
//                        if (flyer.exists()) {
//                            helper.addAttachment(flyer.getName(), new FileSystemResource(flyer));
//                        }
//                    }
//
//                    // Generar QR único por usuario y adjuntarlo usando QrCodeService
//                    try {
//                        String attachName = "qr_" + (partici.getId() != null ? partici.getId() : System.currentTimeMillis()) + ".png";
//                        try {
//                            String qrPath = qrCodeService.saveQRCodeImageToFile(
//                                    String.format("ASISTENCIA|ID:%d|EMAIL:%s|EVENTO:%s", partici.getId(), partici.getEmail(), invitacion.getAsunto()),
//                                    300, 300, attachName
//                            );
//                            File qrFile = new File(qrPath);
//                            if (qrFile.exists()) {
//                                helper.addAttachment(qrFile.getName(), new FileSystemResource(qrFile));
//                            } else {
//                                byte[] qrBytes = qrCodeService.generateQRCodeImage(partici.getId().toString(), 300, 300);
//                                if (qrBytes != null && qrBytes.length > 0) {
//                                    helper.addAttachment(attachName, new ByteArrayResource(qrBytes));
//                                }
//                            }
//                        } catch (Exception e2) {
//                            logger.warn("No se pudo guardar QR en archivo, adjuntando desde bytes: {}", e2.getMessage());
//                            byte[] qrBytes = qrCodeService.generateQRCodeImage(partici.getId().toString(), 300, 300);
//                            if (qrBytes != null && qrBytes.length > 0) {
//                                helper.addAttachment(attachName, new ByteArrayResource(qrBytes));
//                            }
//                        }
//                    } catch (Exception e) {
//                        logger.error("Error generando/adjuntando QR para {}: {}", formatParticipante(partici), e.getMessage(), e);
//                    }
//
//                    if (icsFile.exists()) {
//                        helper.addAttachment(icsFile.getName(), new FileSystemResource(icsFile));
//                    }
//
//                    mailSender.send(mimeMessage);
//                } catch (Exception e) {
//                    System.err.println("Error enviando invitación presencial a " + partici.getEmail() + ": " + e.getMessage());
//                }
//        }
//    }

    // Sobrecarga: enviar invitación presencial para un evento concreto (usa asistencias)
    public void sendInvitacionPresencial(InvitacionPresencial invitacion, Evento evento) throws Exception {
         List<Asistencia> asistenciasEvento = asistenciaRepository.findByEvento(evento);
         String icsPath = CalendarUtil.crearArchivoICS(
                 invitacion.getAsunto(),
                 invitacion.getMensaje(),
                 invitacion.getInicio(),
                 invitacion.getFin(),
                 invitacion.getLugar()
         );
         File icsFile = new File(icsPath);

        for (Asistencia asistencia : asistenciasEvento) {
            try {
                Participante partici = asistencia.getParticipante();
                MimeMessage mimeMessage = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
                helper.setTo(partici.getEmail());
                helper.setSubject(invitacion.getAsunto());
                helper.setText(invitacion.getMensaje().replace("{nombre}", partici.getNombres()), true);

                // Adjuntar flyer si existe
                if (invitacion.getFlyerPath() != null) {
                    File flyer = new File(invitacion.getFlyerPath());
                    if (flyer.exists()) {
                        helper.addAttachment(flyer.getName(), new FileSystemResource(flyer));
                    }
                }

                // Generar QR único por participante y adjuntarlo
                try {
                    String attachName = "qr_" + (partici.getId() != null ? partici.getId() : System.currentTimeMillis()) + ".png";

                    // Contenido que llevaremos al QR (si existe un codigo en la asistencia, lo preferimos)
                    String qrContent = (asistencia.getCodigoQr() != null && !asistencia.getCodigoQr().isBlank())
                            ? asistencia.getCodigoQr()
                            : String.format("ASISTENCIA|ID:%s|EMAIL:%s|EVENTO:%s", (partici.getId() != null ? partici.getId() : "0"), partici.getEmail(), invitacion.getAsunto());

                    String qrPath = null;
                    try {
                        // Intentar guardar directamente en archivo usando el servicio (se espera que guarde en uploads/QR)
                        qrPath = qrCodeService.saveQRCodeImageToFile(qrContent, 250, 250, attachName);
                    } catch (Exception eSave) {
                        logger.warn("qrCodeService.saveQRCodeImageToFile falló: {}", eSave.getMessage());
                    }

                    if (qrPath != null) {
                        File qrFile = new File(qrPath);
                        if (qrFile.exists()) {
                            helper.addAttachment(qrFile.getName(), new FileSystemResource(qrFile));
                        } else {
                            // Fallback: generar bytes y adjuntar
                            byte[] qrBytes = qrCodeService.generateQRCodeImage(qrContent, 250, 250);
                            if (qrBytes != null && qrBytes.length > 0) {
                                helper.addAttachment(attachName, new ByteArrayResource(qrBytes));
                            }
                        }
                    } else {
                        // Si no se generó ruta, adjuntar desde bytes
                        byte[] qrBytes = qrCodeService.generateQRCodeImage(qrContent, 250, 250);
                        if (qrBytes != null && qrBytes.length > 0) {
                            helper.addAttachment(attachName, new ByteArrayResource(qrBytes));
                        }
                    }
                } catch (Exception e) {
                    logger.error("Error generando/adjuntando QR para {}: {}", formatParticipante(asistencia.getParticipante()), e.getMessage(), e);
                }

                if (icsFile.exists()) {
                    helper.addAttachment(icsFile.getName(), new FileSystemResource(icsFile));
                }

                mailSender.send(mimeMessage);
            } catch (Exception e) {
                logger.error("Error enviando invitación presencial a {}: {}", (asistencia.getParticipante() != null ? asistencia.getParticipante().getEmail() : "?"), e.getMessage(), e);
            }
        }
    }

}
