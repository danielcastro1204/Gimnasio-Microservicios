package co.analisys.analytics.infrastructure.streams;

import co.analisys.analytics.domain.event.DatosEntrenamiento;
import co.analisys.analytics.domain.event.ResumenEntrenamiento;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.state.WindowStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.support.serializer.JsonSerde;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Parte 3.3 del taller: stream processor que analiza los datos de
 * entrenamiento. Lee "datos-entrenamiento", agrupa por miembro (la key del
 * mensaje), agrega en ventanas de 7 dias y publica cada resumen en
 * "resumen-entrenamiento". El estado de la agregacion queda en el store
 * "resumen-entrenamiento-store", que se consulta por REST (ver
 * ResumenEntrenamientoQueryService).
 *
 * Recuperacion propia de Kafka Streams: el store se respalda en un topic
 * "changelog" interno en Kafka, y los offsets se confirman junto con el
 * estado; si el proceso se reinicia (aunque pierda su carpeta local),
 * reconstruye el store desde Kafka.
 */
@Configuration
@EnableKafkaStreams
public class KafkaStreamsConfig {

    /** Tolerancia a registros que llegan tarde a una ventana ya "cerrada". */
    private static final Duration GRACIA = Duration.ofHours(1);

    @Bean
    public NewTopic datosEntrenamientoTopic(
            @Value("${gym.kafka.entrenamiento-topic}") String topicName,
            @Value("${gym.kafka.entrenamiento-particiones}") int particiones,
            @Value("${gym.kafka.entrenamiento-retencion-ms}") String retencionMs) {
        // Misma declaracion que en member-service: asi da igual cual arranque primero.
        return TopicBuilder.name(topicName)
                .partitions(particiones)
                .replicas(1)
                .config(TopicConfig.CLEANUP_POLICY_CONFIG, TopicConfig.CLEANUP_POLICY_DELETE)
                .config(TopicConfig.RETENTION_MS_CONFIG, retencionMs)
                .build();
    }

    @Bean
    public NewTopic resumenEntrenamientoTopic(
            @Value("${gym.kafka.resumen-topic}") String topicName,
            @Value("${gym.kafka.entrenamiento-particiones}") int particiones) {
        return TopicBuilder.name(topicName)
                .partitions(particiones)
                .replicas(1)
                .build();
    }

    @Bean
    public KStream<String, DatosEntrenamiento> kStream(
            StreamsBuilder streamsBuilder,
            ObjectMapper objectMapper,
            @Value("${gym.kafka.entrenamiento-topic}") String topicEntrada,
            @Value("${gym.kafka.resumen-topic}") String topicSalida,
            @Value("${gym.kafka.resumen-store}") String nombreStore,
            @Value("${gym.analytics.ventana-dias}") int ventanaDias) {

        // member-service publica sin header de tipo (__TypeId__ apuntaria a una clase que
        // este servicio no tiene): se deserializa siempre hacia la clase local.
        JsonSerde<DatosEntrenamiento> datosSerde =
                new JsonSerde<>(DatosEntrenamiento.class, objectMapper).ignoreTypeHeaders();
        JsonSerde<ResumenEntrenamiento> resumenSerde =
                new JsonSerde<>(ResumenEntrenamiento.class, objectMapper).noTypeInfo();

        Duration ventana = Duration.ofDays(ventanaDias);

        KStream<String, DatosEntrenamiento> stream =
                streamsBuilder.stream(topicEntrada, Consumed.with(Serdes.String(), datosSerde));

        stream.groupByKey(Grouped.with(Serdes.String(), datosSerde))
                .windowedBy(TimeWindows.ofSizeAndGrace(ventana, GRACIA))
                .aggregate(
                        ResumenEntrenamiento::new,
                        (key, value, aggregate) -> aggregate.actualizar(value),
                        Materialized.<String, ResumenEntrenamiento, WindowStore<Bytes, byte[]>>as(nombreStore)
                                .withKeySerde(Serdes.String())
                                .withValueSerde(resumenSerde)
                                // El store debe retener al menos ventana + gracia.
                                .withRetention(ventana.plus(GRACIA)))
                .toStream()
                .map((Windowed<String> clave, ResumenEntrenamiento resumen) -> {
                    resumen.setVentanaInicio(aFecha(clave.window().startTime()));
                    resumen.setVentanaFin(aFecha(clave.window().endTime()));
                    return KeyValue.pair(clave.key(), resumen);
                })
                .to(topicSalida, Produced.with(Serdes.String(), resumenSerde));

        return stream;
    }

    private static LocalDateTime aFecha(java.time.Instant instante) {
        return LocalDateTime.ofInstant(instante, ZoneId.systemDefault());
    }
}
