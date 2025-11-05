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
		// Log de variables de entorno ANTES de iniciar Spring
		System.out.println("==========================================================");
		System.out.println("ENVIRONMENT VARIABLES DEBUG - BEFORE SPRING BOOT START");
		System.out.println("==========================================================");
		System.out.println("SPRING_PROFILES_ACTIVE: " + System.getenv("SPRING_PROFILES_ACTIVE"));
		System.out.println("AIVEN_DATABASE_URL exists: " + (System.getenv("AIVEN_DATABASE_URL") != null));
		System.out.println("AIVEN_DB_USERNAME exists: " + (System.getenv("AIVEN_DB_USERNAME") != null));
		System.out.println("AIVEN_DB_PASSWORD exists: " + (System.getenv("AIVEN_DB_PASSWORD") != null));
		System.out.println("PORT: " + System.getenv("PORT"));
		System.out.println("==========================================================");
		
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
//						.existsByParticipanteIdAndEventoId(participante.getId(), primerEvento.getId());
//
//				if (!existeAsistencia) {
//					Asistencia asistencia = new Asistencia();
//					asistencia.setParticipante(participante);
//					asistencia.setEvento(primerEvento);
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
