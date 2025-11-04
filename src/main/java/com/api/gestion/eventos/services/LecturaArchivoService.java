package com.api.gestion.eventos.services;

import com.api.gestion.eventos.dtos.CargaExcel;
import com.api.gestion.eventos.entities.Asistencia;
import com.api.gestion.eventos.entities.Evento;
import com.api.gestion.eventos.entities.Participante;
import com.api.gestion.eventos.mappers.ParticipanteExcelMapper;
import com.api.gestion.eventos.repositories.EventoRepository;
import com.api.gestion.eventos.repositories.ParticipanteRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j

public class LecturaArchivoService {

    @Autowired
    private ParticipanteRepository participanteRepository;

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private AsistenciaService asistenciaService;



    @Transactional
    public void leerArchivo(CargaExcel cargaExcel) {
        try {
            MultipartFile archivo = cargaExcel.getArchivo();
            Long eventoId = cargaExcel.getEventoId();

            Evento evento = eventoRepository.findById(eventoId)
                    .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

            Workbook workbook = WorkbookFactory.create(archivo.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);

            int participantesCreados = 0;
            int participantesExistentes = 0;
            int asistenciasCreadas = 0;

            for (Row row : sheet) {
                // Saltar fila de encabezados
                if (row.getRowNum() == 0) continue;

                try {
                    // Leer datos según el nuevo orden
                    String nombres = getCellValueAsString(row.getCell(0));
                    String apellidoPaterno = getCellValueAsString(row.getCell(1));
                    String apellidoMaterno = getCellValueAsString(row.getCell(2));
                    String dni = getCellValueAsString(row.getCell(3));
                    String email = getCellValueAsString(row.getCell(4));
                    String numeroWhatsapp = getCellValueAsString(row.getCell(5));
                    String ciudad = getCellValueAsString(row.getCell(6));
                    String rol = getCellValueAsString(row.getCell(7)); // "Soy (este es el rol)"
                    // Columna 8: Modalidad - IGNORADA
                    // Columna 9: Dirección de correo electrónico - Parece duplicado del email

                    // Validar campos obligatorios
                    if (email == null || email.trim().isEmpty()) {
                        log.warn("Fila {} ignorada: email vacío", row.getRowNum() + 1);
                        continue;
                    }

                    // Buscar o crear participante
                    Participante participante = participanteRepository
                            .findByEmail(email.trim())
                            .orElse(null);

                    if (participante == null) {
                        participante = Participante.builder()
                                .nombres(nombres != null ? nombres.trim() : "")
                                .apellidoPaterno(apellidoPaterno != null ? apellidoPaterno.trim() : "")
                                .apellidoMaterno(apellidoMaterno != null ? apellidoMaterno.trim() : "")
                                .dni(dni != null ? dni.trim() : "")
                                .email(email.trim())
                                .numeroWhatsapp(numeroWhatsapp != null ? numeroWhatsapp.trim() : "")
                                .ciudad(ciudad != null ? ciudad.trim() : "")
                                .rol(rol != null ? rol.trim() : "")
                                .build();

                        participante = participanteRepository.save(participante);
                        participantesCreados++;
                        log.info("Participante creado: {}", email);
                    } else {
                        participantesExistentes++;
                        log.info("Participante ya existe: {}", email);
                    }

                    // Crear asistencia si no existe
                    Asistencia asistencia = asistenciaService.crearAsistencia(participante, evento);
                    if (asistencia != null) {
                        asistenciasCreadas++;
                    }

                } catch (Exception e) {
                    log.error("Error procesando fila {}: {}", row.getRowNum() + 1, e.getMessage());
                }
            }

            workbook.close();

            log.info("Importación completada - Participantes creados: {}, existentes: {}, asistencias creadas: {}",
                    participantesCreados, participantesExistentes, asistenciasCreadas);

        } catch (Exception e) {
            log.error("Error al procesar el archivo Excel", e);
            throw new RuntimeException("Error al procesar el archivo: " + e.getMessage());
        }
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return null;

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toString();
                }
                return String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return null;
        }
    }
}