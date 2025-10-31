package com.api.gestion.eventos.config;

import com.api.gestion.eventos.entities.InvitacionPresencial;
import com.api.gestion.eventos.entities.Participante;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;


import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public class QRUtil {
    public static String generateQRCodeForUser(Participante participante, InvitacionPresencial invitacion) {
        try {
            // Contenido del QR: ID del usuario + email + asunto del evento
            String qrContent = String.format("ASISTENCIA|ID:%d|EMAIL:%s|EVENTO:%s",
                    participante.getId(),
                    participante.getEmail(),
                invitacion.getAsunto());

            String filePath = "qr_" + participante.getId() + "_" + System.currentTimeMillis() + ".png";

            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(qrContent, BarcodeFormat.QR_CODE, 300, 300);

            Path path = FileSystems.getDefault().getPath(filePath);
            MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);

            return filePath;
        } catch (WriterException | IOException e) {
            System.err.println("Error generando QR para usuario " + participante.getId() + ": " + e.getMessage());
            return null;
        }
    }
}
