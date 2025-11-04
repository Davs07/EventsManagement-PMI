package com.api.gestion.eventos.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CargaExcel {
    private Long eventoId;
    private MultipartFile archivo;
}
