package com.api.gestion.eventos.services.Certificados;

import com.api.gestion.eventos.entities.Asistencia;
import com.api.gestion.eventos.entities.Certificado;
import com.api.gestion.eventos.entities.Evento;
import com.api.gestion.eventos.repositories.AsistenciaRepository;
import com.api.gestion.eventos.repositories.CertificadoRepository;
import com.api.gestion.eventos.services.EmailService;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Document;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class CertificadoService {

    @Autowired
    private CertificadoRepository certificadoRepository;

    @Autowired
    private AsistenciaRepository asistenciaRepository;

    @Autowired
    private EmailService emailService;

    public byte[] generarCertificadoPDF(Asistencia asistencia, String codigoCertificado)
            throws IOException {

        Evento evento = asistencia.getEvento();

        // Cargar plantilla desde BD o recursos por defecto
        InputStream plantillaStream;
        if (evento.getPlantillaCertificado() != null) {
            plantillaStream = new ByteArrayInputStream(evento.getPlantillaCertificado());
            log.info("Usando plantilla personalizada del evento {}", evento.getId());
        } else {
            ClassPathResource plantilla = new ClassPathResource("plantillas/certificado_plantilla.pdf");
            plantillaStream = plantilla.getInputStream();
            log.info("Usando plantilla por defecto");
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try (PdfDocument pdfDoc = new PdfDocument(
                new PdfReader(plantillaStream),
                new PdfWriter(outputStream))) {

            Document document = new Document(pdfDoc);
            PdfPage page = pdfDoc.getFirstPage();
            PdfCanvas canvas = new PdfCanvas(page);

            PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            canvas.beginText()
                    .setFontAndSize(font, 29f)
                    .setColor(ColorConstants.BLACK, true);

            // Nombre completo
            String nombreCompleto = String.format("%s %s %s",
                    asistencia.getParticipante().getNombres(),
                    asistencia.getParticipante().getApellidoPaterno(),
                    asistencia.getParticipante().getApellidoMaterno()
            );

// Calcular el ancho del texto
            float fontSize = 29f;
            float textWidth = font.getWidth(nombreCompleto, fontSize);

// Calcular posición X centrada
            float xCentro = 421.12f; // tu punto de referencia
            float xInicio = xCentro - (textWidth / 2);

            canvas.beginText()
                    .setFontAndSize(font, fontSize)
                    .setColor(ColorConstants.BLACK, true)
                    .moveText(xInicio, 312.66f)
                    .showText(nombreCompleto)
                    .endText();

            // Rol
            canvas.beginText()
                    .setFontAndSize(font, 14f)
                    .moveText(317.12f, 282.66f)
                    .showText(asistencia.getRol().name())
                    .endText();

            // Código de certificado
            canvas.beginText()
                    .setFontAndSize(font, 12)
                    .setColor(ColorConstants.DARK_GRAY, true)
                    .moveText(592.24, 80)
                    .showText(codigoCertificado)
                    .endText();

            document.close();
        }

        return outputStream.toByteArray();
    }

    @Transactional
    public void enviarCertificadosEvento(Long eventoId) throws Exception {
        List<Asistencia> asistencias = asistenciaRepository.findByEvento_IdAndAsistio(eventoId, true);

        int enviados = 0;
        int errores = 0;

        for (Asistencia asistencia : asistencias) {
            try {
                // Buscar si ya existe un certificado para esta asistencia
                Certificado certificadoExistente = certificadoRepository
                        .findByAsistencia_Id(asistencia.getId())
                        .orElse(null);

                String codigoCertificado;
                Certificado certificado;

                if (certificadoExistente != null) {
                    // Reutilizar código existente
                    codigoCertificado = certificadoExistente.getCodigoCertificado();
                    certificado = certificadoExistente;
                    log.info("Reutilizando certificado existente: {}", codigoCertificado);
                } else {
                    // Generar nuevo código único
                    codigoCertificado = "CIDP-" + UUID.randomUUID().toString()
                            .substring(0, 12).toUpperCase() + "-2025";

                    certificado = Certificado.builder()
                            .codigoCertificado(codigoCertificado)
                            .asistencia(asistencia)
                            .fechaEmision(LocalDateTime.now())
                            .emailEnviado(asistencia.getParticipante().getEmail())
                            .enviado(false)
                            .build();

                    certificadoRepository.save(certificado);
                    log.info("Nuevo certificado generado: {}", codigoCertificado);
                }

                byte[] pdfBytes = generarCertificadoPDF(asistencia, codigoCertificado);

                emailService.enviarCertificado(
                        asistencia.getParticipante().getEmail(),
                        asistencia.getParticipante().getNombres(),
                        pdfBytes,
                        asistencia.getEvento().getNombre()
                );

                certificado.setEnviado(true);
                certificadoRepository.save(certificado);

                enviados++;
                log.info("Certificado enviado a: {}", asistencia.getParticipante().getEmail());

            } catch (Exception e) {
                errores++;
                log.error("Error enviando certificado a {}: {}",
                        asistencia.getParticipante().getEmail(), e.getMessage());
            }
        }

        log.info("Certificados enviados: {}, Errores: {}", enviados, errores);
    }

    public boolean validarCertificado(String codigo) {
        return certificadoRepository.existsByCodigoCertificado(codigo);
    }
}