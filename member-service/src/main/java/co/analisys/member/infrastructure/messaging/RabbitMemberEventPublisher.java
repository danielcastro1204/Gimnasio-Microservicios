package co.analisys.member.infrastructure.messaging;

import co.analisys.member.domain.event.MemberRegisteredEvent;
import co.analisys.member.domain.model.Miembro;
import co.analisys.member.domain.service.MemberEventPublisherPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Adaptador (infrastructure) del puerto MemberEventPublisherPort.
 * Publica de forma asíncrona (fire-and-forget) en el exchange topic
 * "gym.events" con routing key "member.registered"; quien procese la
 * notificación (notification-service) lo hace en su propio tiempo, sin
 * bloquear la respuesta HTTP de member-service.
 */
@Component
public class RabbitMemberEventPublisher implements MemberEventPublisherPort {

    private static final Logger log = LoggerFactory.getLogger(RabbitMemberEventPublisher.class);
    private static final String ROUTING_KEY = "member.registered";

    private final RabbitTemplate rabbitTemplate;
    private final TopicExchange gymEventsExchange;

    public RabbitMemberEventPublisher(RabbitTemplate rabbitTemplate, TopicExchange gymEventsExchange) {
        this.rabbitTemplate = rabbitTemplate;
        this.gymEventsExchange = gymEventsExchange;
    }

    @Override
    public void publicarRegistro(Miembro miembro) {
        MemberRegisteredEvent event = new MemberRegisteredEvent(
                miembro.getId(),
                miembro.getNombre(),
                miembro.getEmail(),
                miembro.getFechaInscripcion(),
                LocalDateTime.now());
        try {
            rabbitTemplate.convertAndSend(gymEventsExchange.getName(), ROUTING_KEY, event);
        } catch (AmqpException e) {
            // La notificación es un efecto secundario, no una regla de negocio: si
            // RabbitMQ no está disponible, el registro del miembro no debe fallar.
            log.warn("No se pudo publicar el evento member.registered para el miembro {}: {}",
                    miembro.getId(), e.getMessage());
        }
    }
}
