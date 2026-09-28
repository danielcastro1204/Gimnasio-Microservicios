package co.analisys.analytics.application.service;

import co.analisys.analytics.domain.event.ResumenEntrenamiento;
import co.analisys.analytics.infrastructure.exception.ResourceNotFoundException;
import co.analisys.analytics.infrastructure.exception.ServiceUnavailableException;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.errors.InvalidStateStoreException;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyWindowStore;
import org.apache.kafka.streams.state.WindowStoreIterator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

/**
 * Consulta interactiva (Interactive Queries) del state store de Kafka Streams:
 * lee directamente el store local "resumen-entrenamiento-store" sin pasar por
 * ninguna base de datos. Al ser un store local, este ejemplo asume UNA sola
 * instancia de analytics-service (suficiente para el taller).
 */
@Service
public class ResumenEntrenamientoQueryService {

    private final StreamsBuilderFactoryBean streamsFactory;
    private final String nombreStore;
    private final Duration ventana;

    public ResumenEntrenamientoQueryService(StreamsBuilderFactoryBean streamsFactory,
                                            @Value("${gym.kafka.resumen-store}") String nombreStore,
                                            @Value("${gym.analytics.ventana-dias}") int ventanaDias) {
        this.streamsFactory = streamsFactory;
        this.nombreStore = nombreStore;
        this.ventana = Duration.ofDays(ventanaDias);
    }

    /** Resumen de la ventana mas reciente del miembro. */
    public ResumenEntrenamiento obtenerResumenActual(Long miembroId) {
        List<ResumenEntrenamiento> historial = obtenerHistorial(miembroId);
        return historial.get(historial.size() - 1);
    }

    /** Un resumen por cada ventana de tiempo en la que el miembro tiene datos (de la mas antigua a la mas reciente). */
    public List<ResumenEntrenamiento> obtenerHistorial(Long miembroId) {
        ReadOnlyWindowStore<String, ResumenEntrenamiento> store = obtenerStore();
        List<ResumenEntrenamiento> resultado = new ArrayList<>();
        try (WindowStoreIterator<ResumenEntrenamiento> it =
                     store.fetch(String.valueOf(miembroId), Instant.EPOCH, Instant.now().plus(Duration.ofDays(1)))) {
            while (it.hasNext()) {
                KeyValue<Long, ResumenEntrenamiento> kv = it.next();
                ResumenEntrenamiento resumen = kv.value;
                // El store guarda solo el valor agregado; el inicio de la ventana es la "key" de la ventana.
                Instant inicio = Instant.ofEpochMilli(kv.key);
                resumen.setVentanaInicio(LocalDateTime.ofInstant(inicio, ZoneId.systemDefault()));
                resumen.setVentanaFin(LocalDateTime.ofInstant(inicio.plus(ventana), ZoneId.systemDefault()));
                resultado.add(resumen);
            }
        } catch (InvalidStateStoreException e) {
            throw new ServiceUnavailableException(
                    "El procesador de streams se esta reiniciando o rebalanceando; intente de nuevo en unos segundos.");
        }
        if (resultado.isEmpty()) {
            throw new ResourceNotFoundException(
                    "Todavia no hay datos de entrenamiento procesados para el miembro con id: " + miembroId);
        }
        return resultado;
    }

    private ReadOnlyWindowStore<String, ResumenEntrenamiento> obtenerStore() {
        KafkaStreams streams = streamsFactory.getKafkaStreams();
        if (streams == null || streams.state() != KafkaStreams.State.RUNNING) {
            throw new ServiceUnavailableException(
                    "Kafka Streams aun no esta en estado RUNNING (esta arrancando o no hay conexion con Kafka).");
        }
        try {
            return streams.store(StoreQueryParameters.fromNameAndType(
                    nombreStore, QueryableStoreTypes.<String, ResumenEntrenamiento>windowStore()));
        } catch (InvalidStateStoreException e) {
            throw new ServiceUnavailableException(
                    "El store '" + nombreStore + "' todavia no esta disponible; intente de nuevo en unos segundos.");
        }
    }
}
