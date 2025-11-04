package com.api.gestion.eventos.web;

import com.api.gestion.eventos.dtos.CargaExcel;
import com.api.gestion.eventos.services.LecturaArchivoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "*") // Ajustar según tu configuración CORS
@RequiredArgsConstructor
@Slf4j
public class CargaArchivoController {

    @Autowired
    private LecturaArchivoService lectureService;

    @PostMapping("/cargar-archivo")
    public ResponseEntity<Map<String, Object>> cargarArchivo(
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam("eventoId") Long eventoId) {

        Map<String, Object> response = new HashMap<>();

        try {
            if (archivo.isEmpty()) {
                response.put("success", false);
                response.put("message", "El archivo está vacío");
                return ResponseEntity.badRequest().body(response);
            }

            // Validar que sea un archivo Excel
            String contentType = archivo.getContentType();
            if (contentType == null ||
                    (!contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") &&
                            !contentType.equals("application/vnd.ms-excel"))) {
                response.put("success", false);
                response.put("message", "El archivo debe ser un Excel (.xlsx o .xls)");
                return ResponseEntity.badRequest().body(response);
            }

            log.info("Procesando archivo: {} para evento ID: {}", archivo.getOriginalFilename(), eventoId);

            // Crear el DTO de carga
            CargaExcel cargaExcel = new CargaExcel();
            cargaExcel.setArchivo(archivo);
            cargaExcel.setEventoId(eventoId);

            // Procesar el archivo
            lectureService.leerArchivo(cargaExcel);

            response.put("success", true);
            response.put("message", "Archivo procesado correctamente");
            response.put("eventoId", eventoId);
            response.put("archivo", archivo.getOriginalFilename());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error al procesar el archivo", e);
            response.put("success", false);
            response.put("message", "Error al procesar el archivo: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}