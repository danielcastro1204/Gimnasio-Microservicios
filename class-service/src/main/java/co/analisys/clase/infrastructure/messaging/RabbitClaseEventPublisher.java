package co.analisys.clase.infrastructure.messaging;

import co.analisys.clase.domain.event.ClassScheduleEvent;
import co.analisys.clase.domain.event.TipoEventoClase;
import co.analisys.clase.domain.model.Clase;
import co.analisys.clase.domain.service.ClaseEventPublisherPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Adaptador (infrastructure) del puerto ClaseEventPublisherPort.
 * Publica de forma asíncrona en el exchange topic "gym.events"; cada tipo de
 * cambio usa una routing key distinta (class.created/updated/cancelled) para
 * que cualquier suscriptor pueda filtrar por wildcard ("class.*") o por evento
 * puntual, sin acoplarse a class-service.
 */
@Component
public class RabbitClaseEventPublisher implements ClaseEventPublisherPort {

    private static final Logger log = LoggerFactory.getLogger(RabbitClaseEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;
    private final TopicExchange gymEventsExchange;

    public RabbitClaseEventPublisher(RabbitTemplate rabbitTemplate, TopicExchange gymEventsExchange) {
        this.rabbitTemplate = rabbitTemplate;
        this.gymEventsExchange = gymEventsExchange;
    }

    @Override
    public void publicarCreacion(Clase clase) {
        publicar(clase, TipoEventoClase.CREATED, "class.created");
    }

    @Override
    public void publicarActualizacion(Clase clase) {
        publicar(clase, TipoEventoClase.UPDATED, "class.updated");
    }

    @Override
    public void publicarCancelacion(Clase clase) {
        publicar(clase, TipoEventoClase.CANCELLED, "class.cancelled");
    }

    private void publicar(Clase clase, TipoEventoClase tipo, String routingKey) {
        ClassScheduleEvent event = new ClassScheduleEvent(
                tipo,
                clase.getId(),
                clase.getNombre(),
                clase.getHorario(),
                clase.getCapacidadMaxima(),
                clase.getEntrenadorId(),
                LocalDateTime.now());
        try {
            rabbitTemplate.convertAndSend(gymEventsExchange.getName(), routingKey, event);
        } catch (AmqpException e) {
            // La notificación es un efecto secundario, no una regla de negocio: si
            // RabbitMQ no está disponible, la operación sobre la clase no debe fallar.
            log.warn("No se pudo publicar el evento {} para la clase {}: {}", routingKey, clase.getId(), e.getMessage());
        }
    }
}
