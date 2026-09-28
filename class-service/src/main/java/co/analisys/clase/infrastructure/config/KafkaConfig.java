package co.analisys.clase.infrastructure.config;

import co.analisys.clase.domain.event.OcupacionClaseEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

/**
 * class-service es el productor del topic "ocupacion-clases": declara el
 * topic (con sus particiones) y expone el KafkaTemplate que usa
 * OcupacionClaseProducer para publicar cada actualización.
 */
@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic ocupacionClasesTopic(@Value("${gym.kafka.ocupacion-topic}") String topicName,
                                          @Value("${gym.kafka.ocupacion-particiones}") int particiones) {
        return TopicBuilder.name(topicName)
                .partitions(particiones)
                .replicas(1)
                .build();
    }

    @Bean
    public KafkaTemplate<String, OcupacionClaseEvent> kafkaTemplate(
            ProducerFactory<String, OcupacionClaseEvent> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }
}
