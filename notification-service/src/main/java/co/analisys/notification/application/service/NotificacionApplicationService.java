package co.analisys.notification.application.service;

import co.analisys.notification.application.dto.NotificacionDTO;
import co.analisys.notification.domain.model.Notificacion;
import co.analisys.notification.domain.model.TipoNotificacion;
import co.analisys.notification.domain.repository.NotificacionRepository;
import co.analisys.notification.infrastructure.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Caso de uso "procesar notificación": simula el envío (lo loguea) y lo deja
 * registrado para poder consultarlo vía REST. Es lo que invocan los listeners
 * de RabbitMQ al consumir un evento de member-service o class-service.
 */
@Service
public class NotificacionApplicationService {

    private static final Logger log = LoggerFactory.getLogger(NotificacionApplicationService.class);

    private final NotificacionRepository notificacionRepository;

    public NotificacionApplicationService(NotificacionRepository notificacionRepository) {
        this.notificacionRepository = notificacionRepository;
    }

    @Transactional
    public NotificacionDTO procesarNotificacion(TipoNotificacion tipo, String destinatario, String mensaje, String origenEvento) {
        Notificacion notificacion = new Notificacion();
        notificacion.setTipo(tipo);
        notificacion.setDestinatario(destinatario);
        notificacion.setMensaje(mensaje);
        notificacion.setOrigenEvento(origenEvento);
        notificacion.setFechaEnvio(LocalDateTime.now());

        Notificacion guardada = notificacionRepository.save(notificacion);

        // "Envío" simulado: en un entorno real aquí se integraría un proveedor de
        // email/SMS/push; para este proyecto académico basta con dejar traza clara.
        log.info("[notification-service] Notificación enviada a '{}' ({}): {}", destinatario, tipo, mensaje);

        return toDTO(guardada);
    }

    public List<NotificacionDTO> obtenerTodas() {
        return notificacionRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public NotificacionDTO obtenerPorId(Long id) {
        Notificacion notificacion = notificacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe una notificación con id: " + id));
        return toDTO(notificacion);
    }

    private NotificacionDTO toDTO(Notificacion n) {
        return new NotificacionDTO(n.getId(), n.getTipo(), n.getDestinatario(), n.getMensaje(), n.getOrigenEvento(), n.getFechaEnvio());
    }
}
