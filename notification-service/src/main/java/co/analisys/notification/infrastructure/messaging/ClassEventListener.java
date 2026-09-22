package co.analisys.notification.infrastructure.messaging;

import co.analisys.notification.application.service.NotificacionApplicationService;
import co.analisys.notification.domain.event.ClassScheduleEvent;
import co.analisys.notification.domain.model.TipoNotificacion;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Suscriptor de eventos "class.*" (created/updated/cancelled): implementa el
 * patrón pub/sub para cambios de horario importantes del gimnasio. La cola
 * está enlazada con el wildcard "class.*", así que cualquier routing key
 * nueva que empiece con "class." llega aquí sin tocar esta configuración.
 */
@Component
public class ClassEventListener {

    private final NotificacionApplicationService notificacionApplicationService;

    public ClassEventListener(NotificacionApplicationService notificacionApplicationService) {
        this.notificacionApplicationService = notificacionApplicationService;
    }

    @RabbitListener(queues = "${notification.queues.class-events}")
    public void onClassEvent(ClassScheduleEvent event) {
        String destinatario = "entrenador:" + event.getEntrenadorId();

        switch (event.getTipoEvento()) {
            case "CREATED" -> notificacionApplicationService.procesarNotificacion(
                    TipoNotificacion.CLASE_PROGRAMADA, destinatario,
                    mensajeProgramada(event), "class.created");
            case "UPDATED" -> notificacionApplicationService.procesarNotificacion(
                    TipoNotificacion.CLASE_ACTUALIZADA, destinatario,
                    mensajeActualizada(event), "class.updated");
            case "CANCELLED" -> notificacionApplicationService.procesarNotificacion(
                    TipoNotificacion.CLASE_CANCELADA, destinatario,
                    mensajeCancelada(event), "class.cancelled");
            default -> { /* tipo de evento desconocido: se ignora de forma segura */ }
        }
    }

    private String mensajeProgramada(ClassScheduleEvent e) {
        return String.format("Nueva clase programada: '%s' el %s (capacidad %d).", e.getNombre(), e.getHorario(), e.getCapacidadMaxima());
    }

    private String mensajeActualizada(ClassScheduleEvent e) {
        return String.format("La clase '%s' fue reprogramada. Nuevo horario: %s (capacidad %d).", e.getNombre(), e.getHorario(), e.getCapacidadMaxima());
    }

    private String mensajeCancelada(ClassScheduleEvent e) {
        return String.format("La clase '%s' programada para el %s fue cancelada.", e.getNombre(), e.getHorario());
    }
}
