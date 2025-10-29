package com.api.gestion.eventos.services;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class QrCodeService {

    public byte[] generateQRCodeImage(String text, int width, int height) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        byte[] pngData = pngOutputStream.toByteArray();

        return pngData;
    }

    // Nuevo método: generar y guardar a un archivo con nombre específico en uploads/QR/
    public String saveQRCodeImageToFile(String text, int width, int height, String fileName) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

        Path dir = Paths.get("uploads", "QR");
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }

        // asegurar que fileName no contiene rutas peligrosas
        String safeName = (fileName == null || fileName.isBlank()) ? ("qr_" + System.currentTimeMillis() + ".png") : fileName.replaceAll("[\\/:*?\"<>|]", "_");
        Path filePath = dir.resolve(safeName);

        // escribir directamente a archivo
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", baos);
            Files.write(filePath, baos.toByteArray());
        }

        return filePath.toString();
    }

    //para decodificar el QR
    public String decodeQRCode(byte[] qrCodeImage) throws Exception {
        BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(qrCodeImage));

        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(
                new BufferedImageLuminanceSource(bufferedImage)));

        Result result = new MultiFormatReader().decode(binaryBitmap);
        return result.getText();
    }
}