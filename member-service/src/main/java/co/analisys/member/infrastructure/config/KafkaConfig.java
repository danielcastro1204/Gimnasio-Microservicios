package co.analisys.member.infrastructure.config;

import co.analisys.member.domain.event.DatosEntrenamientoEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

/**
 * member-service es el productor del topic "datos-entrenamiento".
 * Declara el topic con su retencion de log (Parte 3.4 del taller): mientras
 * el log conserve los mensajes, cualquier consumidor puede releerlos desde
 * un offset anterior para recuperarse de un fallo.
 * (analytics-service declara el mismo topic con la misma configuracion, asi
 * no importa cual de los dos servicios arranque primero.)
 */
@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic datosEntrenamientoTopic(
            @Value("${gym.kafka.entrenamiento-topic}") String topicName,
            @Value("${gym.kafka.entrenamiento-particiones}") int particiones,
            @Value("${gym.kafka.entrenamiento-retencion-ms}") String retencionMs) {
        return TopicBuilder.name(topicName)
                .partitions(particiones)
                .replicas(1)
                .config(TopicConfig.CLEANUP_POLICY_CONFIG, TopicConfig.CLEANUP_POLICY_DELETE)
                .config(TopicConfig.RETENTION_MS_CONFIG, retencionMs)
                .build();
    }

    @Bean
    public KafkaTemplate<String, DatosEntrenamientoEvent> kafkaTemplate(
            ProducerFactory<String, DatosEntrenamientoEvent> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }
}
