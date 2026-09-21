package co.analisys.notification.infrastructure.messaging;

import co.analisys.notification.application.service.NotificacionApplicationService;
import co.analisys.notification.domain.event.MemberRegisteredEvent;
import co.analisys.notification.domain.model.TipoNotificacion;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Suscriptor del evento "member.registered": es el sistema de notificación
 * asíncrona de nuevos suscriptores. Se ejecuta en su propio hilo/consumidor,
 * totalmente desacoplado del request HTTP que registró al miembro.
 */
@Component
public class MemberEventListener {

    private final NotificacionApplicationService notificacionApplicationService;

    public MemberEventListener(NotificacionApplicationService notificacionApplicationService) {
        this.notificacionApplicationService = notificacionApplicationService;
    }

    @RabbitListener(queues = "${notification.queues.member-registered}")
    public void onMemberRegistered(MemberRegisteredEvent event) {
        String mensaje = String.format(
                "¡Bienvenido/a %s! Tu inscripción al gimnasio quedó registrada el %s.",
                event.getNombre(), event.getFechaInscripcion());
        notificacionApplicationService.procesarNotificacion(
                TipoNotificacion.BIENVENIDA_MIEMBRO, event.getEmail(), mensaje, "member.registered");
    }
}
