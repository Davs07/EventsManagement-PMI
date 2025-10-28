package com.api.gestion.eventos;

import com.api.gestion.eventos.entities.Asistencia;
import com.api.gestion.eventos.entities.Evento;
import com.api.gestion.eventos.entities.Participante;
import com.api.gestion.eventos.enums.RolParticipante;
import com.api.gestion.eventos.repositories.AsistenciaRepository;
import com.api.gestion.eventos.repositories.EventoRepository;
import com.api.gestion.eventos.repositories.ParticipanteRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}


//	@Bean
//	CommandLineRunner start(AsistenciaRepository asistenciaRepository,
//							EventoRepository eventoRepository,
//							ParticipanteRepository participanteRepository) {
//		return args -> {
//			// Validar que existan eventos
//			List<Evento> eventos = eventoRepository.findAll();
//			if (eventos.isEmpty()) {
//				System.out.println("No hay eventos disponibles para crear asistencias");
//				return;
//			}
//
//			Evento primerEvento = eventos.get(0);
//			Evento segundoEvento = eventos.get(1);
//			List<Participante> participantes = participanteRepository.findAll();
//
//			for (Participante participante : participantes.subList(0, Math.min(participantes.size(), 10))) {
//				// Verificar si ya existe asistencia para este participante y evento
//				boolean existeAsistencia = asistenciaRepository
//						.existsByParticipanteIdAndEventoId(participante.getId(), segundoEvento.getId());
//
//				if (!existeAsistencia) {
//					Asistencia asistencia = new Asistencia();
//					asistencia.setParticipante(participante);
//					asistencia.setEvento(segundoEvento);
//					asistencia.setCodigoQr(UUID.randomUUID().toString());
//					asistencia.setRol(RolParticipante.ASISTENTE);
//					asistencia.setAsistio(false);
//					asistenciaRepository.save(asistencia);
//					System.out.println("Asistencia creada para: " + participante.getNombres());
//				}
//			}
//		};
//	}
}
