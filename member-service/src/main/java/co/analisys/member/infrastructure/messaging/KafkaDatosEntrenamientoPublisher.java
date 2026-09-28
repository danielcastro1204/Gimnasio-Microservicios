package co.analisys.member.infrastructure.messaging;

import co.analisys.member.domain.event.DatosEntrenamientoEvent;
import co.analisys.member.domain.service.DatosEntrenamientoPublisherPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Publica cada sesion de entrenamiento en "datos-entrenamiento". La key es
 * el id del miembro: Kafka garantiza el orden dentro de una particion y
 * Kafka Streams puede agrupar (groupByKey) sin reparticionar.
 */
@Component
public class KafkaDatosEntrenamientoPublisher implements DatosEntrenamientoPublisherPort {

    private static final Logger log = LoggerFactory.getLogger(KafkaDatosEntrenamientoPublisher.class);

    private final KafkaTemplate<String, DatosEntrenamientoEvent> kafkaTemplate;
    private final String topic;

    public KafkaDatosEntrenamientoPublisher(KafkaTemplate<String, DatosEntrenamientoEvent> kafkaTemplate,
                                            @Value("${gym.kafka.entrenamiento-topic}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    @Override
    public void publicar(DatosEntrenamientoEvent evento) {
        kafkaTemplate.send(topic, String.valueOf(evento.getMiembroId()), evento)
                .whenComplete((resultado, error) -> {
                    if (error != null) {
                        log.error("No se pudo publicar en '{}' el entrenamiento del miembro {}",
                                topic, evento.getMiembroId(), error);
                    } else {
                        log.info("Publicado en '{}' (particion {}, offset {}): miembro {} - {} ({} min)",
                                topic, resultado.getRecordMetadata().partition(),
                                resultado.getRecordMetadata().offset(),
                                evento.getMiembroId(), evento.getEjercicio(), evento.getDuracionMinutos());
                    }
                });
    }
}
