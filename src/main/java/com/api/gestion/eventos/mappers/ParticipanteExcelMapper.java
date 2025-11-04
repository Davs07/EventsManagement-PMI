package com.api.gestion.eventos.mappers;

import com.api.gestion.eventos.entities.Participante;
import org.apache.poi.ss.usermodel.*;

import java.util.ArrayList;
import java.util.List;

public class ParticipanteExcelMapper {
    public static Participante mapearDesdeExcel(Row row) {
        if (row == null) {
            return null;
        }

        return Participante.builder()
                // Columna 1: Nombres
                .nombres(getCellValueAsString(row.getCell(1)))

                // Columna 2: Apellido Paterno
                .apellidoPaterno(getCellValueAsString(row.getCell(2)))

                // Columna 3: Apellido Materno
                .apellidoMaterno(getCellValueAsString(row.getCell(3)))

                // Columna 4: DNI
                .dni(getCellValueAsString(row.getCell(4)))

                // Columna 5: Email
                .email(limpiarEmail(getCellValueAsString(row.getCell(5))))

                // Columna 6: Número de Whatsapp
                .numeroWhatsapp(getCellValueAsString(row.getCell(6)))

                // Columna 7: Ciudad
                .ciudad(getCellValueAsString(row.getCell(7)))

                // Columna 8: Soy (Estudiante/Miembro/etc) → rol
                .rol(getCellValueAsString(row.getCell(8)))

                // Columna 10: Estudiante programaEstudio
                .especialidad(getCellValueAsString(row.getCell(10)))

                // Columna 11: Institución Educativa
                .ieEducativa(getCellValueAsString(row.getCell(11)))

                // Columna 12: Evidencia de Estudios
                .evidenciaEstudio(getCellValueAsString(row.getCell(12)))

                // Columna 13: Capítulo PMI
                .capituloPmi(getCellValueAsString(row.getCell(13)))

                // Columna 14: ID Miembro PMI
                .idMiembroPmi(getCellValueAsString(row.getCell(14)))

                //   Esto requeriría descargar el archivo de la URL para convertirlo a bytes

                .build();
    }

    public static List<Participante> mapearListaDesdeExcel(Sheet sheet) {
        List<Participante> participantes = new ArrayList<>();

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            Participante participante = mapearDesdeExcel(row);
            if (participante != null && participante.getEmail() != null) {
                participantes.add(participante);
            }
        }

        return participantes;
    }

    private static String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return null;
        }

        switch (cell.getCellType()) {
            case STRING:
                String value = cell.getStringCellValue();
                return value != null && !value.trim().isEmpty() ? value.trim() : null;
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf((long) cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
                return null;
            default:
                return null;
        }
    }

    private static String limpiarEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }
        return email.trim().toLowerCase();
    }

    private static boolean parsearBoolean(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return false;
        }

        String valorLimpio = valor.trim().toLowerCase();
        return valorLimpio.contains("sí") ||
                valorLimpio.contains("si") ||
                valorLimpio.equals("yes") ||
                (!valorLimpio.contains("no") && !valorLimpio.isEmpty());
    }
}