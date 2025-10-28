package com.api.gestion.eventos.services;

import com.api.gestion.eventos.entities.Asistencia;

import java.util.List;

public interface AsistenciaService {
    Asistencia crearAsistencia(Asistencia asistencia);
    Asistencia obtenerAsistenciaPorId(Long id);
    List<Asistencia> obtenerAsistenciasPorUsuario(Long usuarioId);
    List<Asistencia> obtenerAsistenciasPorEvento(Long eventoId);
    Asistencia actualizarAsistencia(Long id, Asistencia asistencia);
    Asistencia registrarAsistencia(Long id);
    void eliminarAsistencia(Long id);

    // Nuevos métodos para QR
    Asistencia registrarAsistenciaPorQR(String codigoQr);
    byte[] generarQRParaAsistencia(Long asistenciaId) throws Exception;

    //Metodos para marcar asistencia desde la lista de frontend
    Asistencia obtenerAsistenciaPorParticipanteYEvento(Long participanteId, Long eventoId);
    Asistencia actualizarEstadoAsistencia(Long participanteId, Long eventoId, boolean asistio);

}
