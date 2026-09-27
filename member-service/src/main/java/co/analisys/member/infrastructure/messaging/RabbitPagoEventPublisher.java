package co.analisys.member.infrastructure.messaging;

import co.analisys.member.domain.event.PagoEvent;
import co.analisys.member.domain.service.PagoEventPublisherPort;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * A diferencia de RabbitMemberEventPublisher (que publica en el exchange
 * topic "gym.events" para pub/sub), esto es una cola de trabajo punto a
 * punto: se publica directo en "pagos-queue" usando el exchange por defecto
 * ("") con la routing key igual al nombre de la cola, tal como lo hace el
 * ejemplo de la guía del taller.
 */
@Component
public class RabbitPagoEventPublisher implements PagoEventPublisherPort {

    private final RabbitTemplate rabbitTemplate;
    private final Queue pagosQueue;

    public RabbitPagoEventPublisher(RabbitTemplate rabbitTemplate, Queue pagosQueue) {
        this.rabbitTemplate = rabbitTemplate;
        this.pagosQueue = pagosQueue;
    }

    @Override
    public void publicarPagoPendiente(Long pagoId, Long miembroId, boolean simularFallo) {
        PagoEvent event = new PagoEvent(pagoId, miembroId, simularFallo);
        // A propósito NO se envuelve en try/catch como el evento de member.registered:
        // un pago sí debe fallar de forma visible si RabbitMQ no está disponible,
        // en vez de quedar "fantasma" en PENDIENTE para siempre.
        rabbitTemplate.convertAndSend("", pagosQueue.getName(), event);
    }
}
