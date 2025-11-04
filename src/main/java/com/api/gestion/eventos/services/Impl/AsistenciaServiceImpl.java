package com.api.gestion.eventos.services.Impl;

import com.api.gestion.eventos.dtos.AsistenciaDTO;
import com.api.gestion.eventos.entities.Asistencia;
import com.api.gestion.eventos.entities.Evento;
import com.api.gestion.eventos.entities.Participante;
import com.api.gestion.eventos.mappers.AsistenciaMapper;
import com.api.gestion.eventos.repositories.AsistenciaRepository;
import com.api.gestion.eventos.repositories.EventoRepository;
import com.api.gestion.eventos.repositories.ParticipanteRepository;
import com.api.gestion.eventos.services.AsistenciaService;
import com.api.gestion.eventos.services.QrCodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
@Slf4j
@Service
@Transactional
public class AsistenciaServiceImpl implements AsistenciaService {
    @Autowired
    private AsistenciaRepository asistenciaRepository;

    @Autowired
    private ParticipanteRepository usuarioRepository;

    @Autowired
    private EventoRepository eventoRepository;
    //Para QR:
    @Autowired
    private QrCodeService qrCodeService;
    @Autowired
    private AsistenciaMapper asistenciaMapper;

    @Override
    public Asistencia crearAsistencia(Asistencia asistencia) {
        // Cargar las entidades completas
        Participante usuarioCompleto = usuarioRepository.findById(asistencia.getParticipante().getId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Evento eventoCompleto = eventoRepository.findById(asistencia.getEvento().getId())
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

        // Verificar si ya existe una asistencia para este participante en este evento
        if (asistenciaRepository.existsByParticipanteAndEvento(usuarioCompleto, eventoCompleto)) {
            throw new IllegalStateException("El participante ya está registrado en este evento");
        }

        // Asignar entidades completas
        asistencia.setParticipante(usuarioCompleto);
        asistencia.setEvento(eventoCompleto);

        // Asignar rol por defecto si no viene especificado
        if (asistencia.getRol() == null) {
            asistencia.setRol(com.api.gestion.eventos.enums.RolParticipante.ASISTENTE);
        }

        // Generar código QR único
        asistencia.setCodigoQr(UUID.randomUUID().toString());
        asistencia.setAsistio(false);
        asistencia.setFechaRegistro(LocalDateTime.now());

        return asistenciaRepository.save(asistencia);
    }

    @Override
    public Asistencia obtenerAsistenciaPorId(Long id) {
        return asistenciaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asistencia no encontrada con id: " + id));
    }

    @Override
    public List<Asistencia> obtenerAsistenciasPorUsuario(Long usuarioId) {
        Participante usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + usuarioId));
        return asistenciaRepository.findByParticipante(usuario);
    }

    @Override
    public List<Asistencia> obtenerAsistenciasPorEvento(Long eventoId) {
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado con id: " + eventoId));
        return asistenciaRepository.findByEvento(evento);
    }

    @Override
    public Asistencia actualizarAsistencia(Long id, Asistencia asistencia) {
        asistencia.setId(id);
        return asistenciaRepository.save(asistencia);
    }

    @Override
    public Asistencia registrarAsistencia(Long id) {
        Asistencia asistencia = obtenerAsistenciaPorId(id);
        asistencia.setAsistio(true);
        asistencia.setHoraIngreso(LocalDateTime.now());
        return asistenciaRepository.save(asistencia);
    }

    @Override
    public void eliminarAsistencia(Long id) {
        // Primero obtener la asistencia para asegurar que existe
        Asistencia asistencia = asistenciaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asistencia no encontrada con id: " + id));

        // Eliminar usando el objeto en lugar del ID
        asistenciaRepository.delete(asistencia);
    }

        //PARA asistencias con QRs
    @Override
    public Asistencia registrarAsistenciaPorQR(String codigoQr) {
        Asistencia asistencia = asistenciaRepository.findByCodigoQr(codigoQr)
                .orElseThrow(() -> new RuntimeException("No se encontró asistencia con el código QR: " + codigoQr));

        if (asistencia.getAsistio()==true) {
            throw new IllegalStateException("La asistencia ya fue registrada previamente");
        }

        asistencia.setAsistio(true);
        asistencia.setHoraIngreso(LocalDateTime.now());
        return asistenciaRepository.save(asistencia);
    }

    @Override
    public byte[] generarQRParaAsistencia(Long asistenciaId) throws Exception {
        Asistencia asistencia = obtenerAsistenciaPorId(asistenciaId);
        return qrCodeService.generateQRCodeImage(asistencia.getCodigoQr(), 250, 250);
    }

        //Metodos para marcar asistencia desde la lista de frontend

    @Override
    public Asistencia obtenerAsistenciaPorParticipanteYEvento(Long participanteId, Long eventoId) {
        return asistenciaRepository.findByParticipanteIdAndEventoId(participanteId, eventoId)
                .orElseThrow(() -> new RuntimeException("No se encontró asistencia para este participante en el evento"));
    }

    @Override
    public Asistencia actualizarEstadoAsistencia(Long participanteId, Long eventoId, boolean asistio) {
        Asistencia asistencia = obtenerAsistenciaPorParticipanteYEvento(participanteId, eventoId);
        asistencia.setAsistio(asistio);
        if (asistio && asistencia.getHoraIngreso() == null) {
            asistencia.setHoraIngreso(LocalDateTime.now());
        }
        return asistenciaRepository.save(asistencia);
    }

    @Override
    @Transactional
    public Asistencia crearAsistencia(Participante participante, Evento evento) {
        // Verificar si ya existe una asistencia para este participante en este evento
        if (asistenciaRepository.existsByParticipanteAndEvento(participante, evento)) {
            log.info("Ya existe asistencia para participante {} en evento {}",
                    participante.getEmail(), evento.getId());
            return null;
        }

        // Crear la asistencia usando el mapper
        Asistencia asistencia = AsistenciaMapper.crearAsistencia(participante, evento);

        // Guardar la asistencia
        Asistencia asistenciaGuardada = asistenciaRepository.save(asistencia);

        log.info("Asistencia creada: ID {} - Participante: {} - Evento: {}",
                asistenciaGuardada.getId(),
                participante.getEmail(),
                evento.getNombre());

        return asistenciaGuardada;
    }

    @Override
    public void borrarAsistencia(AsistenciaDTO asistenciaDto) {

    }
}
