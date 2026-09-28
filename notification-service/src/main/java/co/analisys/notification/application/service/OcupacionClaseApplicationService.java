package co.analisys.notification.application.service;

import co.analisys.notification.application.dto.OcupacionClaseDTO;
import co.analisys.notification.domain.event.OcupacionClaseEvent;
import co.analisys.notification.domain.model.OcupacionClase;
import co.analisys.notification.domain.repository.OcupacionClaseRepository;
import co.analisys.notification.infrastructure.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Caso de uso "actualizar dashboard de ocupación": lo invoca
 * OcupacionClaseConsumer al consumir cada mensaje de "ocupacion-clases".
 * Hace upsert por claseId: guarda siempre el último estado conocido.
 */
@Service
public class OcupacionClaseApplicationService {

    private static final Logger log = LoggerFactory.getLogger(OcupacionClaseApplicationService.class);

    private final OcupacionClaseRepository ocupacionClaseRepository;

    public OcupacionClaseApplicationService(OcupacionClaseRepository ocupacionClaseRepository) {
        this.ocupacionClaseRepository = ocupacionClaseRepository;
    }

    @Transactional
    public void actualizarDashboard(OcupacionClaseEvent evento) {
        OcupacionClase snapshot = ocupacionClaseRepository.findByClaseId(evento.getClaseId())
                .orElseGet(OcupacionClase::new);

        snapshot.setClaseId(evento.getClaseId());
        snapshot.setNombreClase(evento.getNombreClase());
        snapshot.setOcupacionActual(evento.getOcupacionActual());
        snapshot.setCapacidadMaxima(evento.getCapacidadMaxima());
        snapshot.setUltimaActualizacion(evento.getTimestamp());

        ocupacionClaseRepository.save(snapshot);

        log.info("[notification-service] Dashboard actualizado: clase {} ({}) en {}/{}",
                evento.getClaseId(), evento.getNombreClase(), evento.getOcupacionActual(), evento.getCapacidadMaxima());
    }

    public List<OcupacionClaseDTO> obtenerTodas() {
        return ocupacionClaseRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public OcupacionClaseDTO obtenerPorClaseId(Long claseId) {
        OcupacionClase snapshot = ocupacionClaseRepository.findByClaseId(claseId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No hay datos de ocupación para la clase con id: " + claseId));
        return toDTO(snapshot);
    }

    private OcupacionClaseDTO toDTO(OcupacionClase o) {
        return new OcupacionClaseDTO(o.getClaseId(), o.getNombreClase(), o.getOcupacionActual(),
                o.getCapacidadMaxima(), o.getUltimaActualizacion());
    }
}
