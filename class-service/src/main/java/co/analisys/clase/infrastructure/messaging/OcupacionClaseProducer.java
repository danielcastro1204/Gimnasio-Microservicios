package co.analisys.clase.infrastructure.messaging;

import co.analisys.clase.domain.event.OcupacionClaseEvent;
import co.analisys.clase.domain.model.Clase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Publica en el topic "ocupacion-clases" cada vez que cambia la ocupación
 * actual de una clase. Se usa el id de la clase como key del mensaje para
 * que Kafka garantice el orden de las actualizaciones de una misma clase
 * (siempre van a la misma partición).
 */
@Component
public class OcupacionClaseProducer {

    private static final Logger log = LoggerFactory.getLogger(OcupacionClaseProducer.class);

    private final KafkaTemplate<String, OcupacionClaseEvent> kafkaTemplate;
    private final String topic;

    public OcupacionClaseProducer(KafkaTemplate<String, OcupacionClaseEvent> kafkaTemplate,
                                   @Value("${gym.kafka.ocupacion-topic}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void publicarActualizacion(Clase clase) {
        OcupacionClaseEvent evento = new OcupacionClaseEvent(
                clase.getId(),
                clase.getNombre(),
                clase.getOcupacionActual(),
                clase.getCapacidadMaxima(),
                LocalDateTime.now());

        kafkaTemplate.send(topic, String.valueOf(clase.getId()), evento);
        log.info("Publicado en '{}': clase {} ahora en {}/{}",
                topic, clase.getId(), clase.getOcupacionActual(), clase.getCapacidadMaxima());
    }
}
