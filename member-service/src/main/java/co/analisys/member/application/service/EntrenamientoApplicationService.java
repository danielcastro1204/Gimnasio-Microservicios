package co.analisys.member.application.service;

import co.analisys.member.application.dto.RegistrarEntrenamientoRequest;
import co.analisys.member.domain.event.DatosEntrenamientoEvent;
import co.analisys.member.domain.repository.MiembroRepository;
import co.analisys.member.domain.service.DatosEntrenamientoPublisherPort;
import co.analisys.member.infrastructure.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Caso de uso "registrar una sesion de entrenamiento": valida que el
 * miembro exista y publica el dato en Kafka. member-service no guarda el
 * dato: el analisis (ventanas de 7 dias) vive en analytics-service.
 */
@Service
public class EntrenamientoApplicationService {

    private final MiembroRepository miembroRepository;
    private final DatosEntrenamientoPublisherPort publisher;

    public EntrenamientoApplicationService(MiembroRepository miembroRepository,
                                           DatosEntrenamientoPublisherPort publisher) {
        this.miembroRepository = miembroRepository;
        this.publisher = publisher;
    }

    public DatosEntrenamientoEvent registrar(Long miembroId, RegistrarEntrenamientoRequest request) {
        if (!miembroRepository.existsById(miembroId)) {
            throw new ResourceNotFoundException("No existe un miembro con id: " + miembroId);
        }
        DatosEntrenamientoEvent evento = new DatosEntrenamientoEvent(
                miembroId,
                request.getEjercicio(),
                request.getDuracionMinutos(),
                request.getCaloriasQuemadas(),
                request.getFrecuenciaCardiacaPromedio(),
                request.getFecha() != null ? request.getFecha() : LocalDateTime.now());
        publisher.publicar(evento);
        return evento;
    }
}
