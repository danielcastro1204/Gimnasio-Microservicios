package co.analisys.clase.application.service;

import co.analisys.clase.application.dto.ClaseDTO;
import co.analisys.clase.application.dto.EntrenadorInfoDTO;
import co.analisys.clase.domain.model.Clase;
import co.analisys.clase.domain.repository.ClaseRepository;
import co.analisys.clase.domain.service.ClaseDomainService;
import co.analisys.clase.domain.service.ClaseEventPublisherPort;
import co.analisys.clase.domain.service.EntrenadorInfo;
import co.analisys.clase.domain.service.EntrenadorVerificationPort;
import co.analisys.clase.infrastructure.exception.BusinessRuleException;
import co.analisys.clase.infrastructure.exception.ResourceNotFoundException;
import co.analisys.clase.infrastructure.messaging.OcupacionClaseProducer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Servicio de aplicación del bounded context "Scheduling".
 * Orquesta los casos de uso de programación/reprogramación/cancelación de
 * clases (incluye validación cruzada con trainer-service), el enriquecimiento
 * de lectura con datos del entrenador, y la publicación de eventos de cambio
 * de horario para el resto del sistema (pub/sub vía gym.events).
 */
@Service
public class ClaseApplicationService {

    private final ClaseRepository claseRepository;
    private final ClaseDomainService claseDomainService;
    private final EntrenadorVerificationPort entrenadorVerificationPort;
    private final ClaseEventPublisherPort claseEventPublisherPort;
    private final OcupacionClaseProducer ocupacionClaseProducer;

    public ClaseApplicationService(ClaseRepository claseRepository,
                                    ClaseDomainService claseDomainService,
                                    EntrenadorVerificationPort entrenadorVerificationPort,
                                    ClaseEventPublisherPort claseEventPublisherPort,
                                    OcupacionClaseProducer ocupacionClaseProducer) {
        this.claseRepository = claseRepository;
        this.claseDomainService = claseDomainService;
        this.entrenadorVerificationPort = entrenadorVerificationPort;
        this.claseEventPublisherPort = claseEventPublisherPort;
        this.ocupacionClaseProducer = ocupacionClaseProducer;
    }

    @Transactional
    public ClaseDTO programarClase(ClaseDTO dto) {
        Clase clase = toEntity(dto);
        claseDomainService.validarProgramacion(clase);
        Clase guardada = claseRepository.save(clase);
        claseEventPublisherPort.publicarCreacion(guardada);
        return toDTO(guardada, true);
    }

    @Transactional
    public ClaseDTO actualizarClase(Long id, ClaseDTO dto) {
        Clase existente = claseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe una clase con id: " + id));
        existente.setNombre(dto.getNombre());
        existente.setHorario(dto.getHorario());
        existente.setCapacidadMaxima(dto.getCapacidadMaxima());
        existente.setEntrenadorId(dto.getEntrenadorId());
        claseDomainService.validarActualizacion(existente);
        Clase actualizada = claseRepository.save(existente);
        claseEventPublisherPort.publicarActualizacion(actualizada);
        return toDTO(actualizada, true);
    }

    @Transactional
    public void cancelarClase(Long id) {
        Clase existente = claseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe una clase con id: " + id));
        claseRepository.delete(existente);
        claseEventPublisherPort.publicarCancelacion(existente);
    }

    /**
     * Caso de uso de la Parte 3.1 del taller: actualiza cuántos asistentes
     * hay ahora mismo en una clase y publica el cambio en el topic Kafka
     * "ocupacion-clases" para que notification-service alimente su dashboard
     * en tiempo real.
     */
    @Transactional
    public ClaseDTO actualizarOcupacion(Long id, int nuevaOcupacion) {
        Clase clase = claseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe una clase con id: " + id));

        if (nuevaOcupacion > clase.getCapacidadMaxima()) {
            throw new BusinessRuleException(
                    "La ocupación (" + nuevaOcupacion + ") no puede superar la capacidad máxima (" +
                            clase.getCapacidadMaxima() + ") de la clase " + id);
        }

        clase.setOcupacionActual(nuevaOcupacion);
        Clase actualizada = claseRepository.save(clase);

        ocupacionClaseProducer.publicarActualizacion(actualizada);

        return toDTO(actualizada, true);
    }

    public List<ClaseDTO> obtenerTodasClases() {
        return claseRepository.findAll().stream()
                .map(c -> toDTO(c, true))
                .collect(Collectors.toList());
    }

    public ClaseDTO obtenerClasePorId(Long id) {
        Clase clase = claseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe una clase con id: " + id));
        return toDTO(clase, true);
    }

    private Clase toEntity(ClaseDTO dto) {
        Clase c = new Clase();
        c.setId(dto.getId());
        c.setNombre(dto.getNombre());
        c.setHorario(dto.getHorario());
        c.setCapacidadMaxima(dto.getCapacidadMaxima());
        c.setEntrenadorId(dto.getEntrenadorId());
        return c;
    }

    private ClaseDTO toDTO(Clase c, boolean enriquecer) {
        ClaseDTO dto = new ClaseDTO(c.getId(), c.getNombre(), c.getHorario(), c.getCapacidadMaxima(), c.getEntrenadorId());
        dto.setOcupacionActual(c.getOcupacionActual());
        if (enriquecer) {
            Optional<EntrenadorInfo> info = entrenadorVerificationPort.obtenerEntrenador(c.getEntrenadorId());
            info.ifPresent(i -> dto.setEntrenador(new EntrenadorInfoDTO(i.getId(), i.getNombre(), i.getEspecialidad())));
        }
        return dto;
    }
}
