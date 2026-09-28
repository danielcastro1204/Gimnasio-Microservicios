package co.analisys.analytics.infrastructure.messaging;

import co.analisys.analytics.application.dto.RecuperacionEstadoDTO;
import co.analisys.analytics.application.service.ProcesadorEntrenamientoService;
import jakarta.annotation.PreDestroy;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Parte 4 del taller: mecanismo de recuperacion ante fallos usando el log de Kafka.
 *
 * Idea: el topic "datos-entrenamiento" es un log que Kafka conserva durante la
 * retencion configurada (30 dias). Este servicio construye una vista propia
 * (tabla entrenamientos_registrados) leyendo ese log, y despues de CADA registro
 * guarda un checkpoint (topic, particion, offset) en la base de datos, en la
 * misma transaccion (ver ProcesadorEntrenamientoService).
 *
 * - Si el proceso se cae o falla el procesamiento: se reabre el consumidor y se
 *   posiciona en checkpoint + 1 de cada particion (reanuda sin perder ni duplicar).
 * - Si se pierde la base de datos (sin checkpoints): se reproduce el log desde el
 *   primer offset disponible y el estado se reconstruye completo.
 * - Si el checkpoint apunta a datos que la retencion ya borro: se avisa y se
 *   arranca desde el primer offset que aun existe.
 *
 * Diferencia con el ejemplo de la guia: se usa assign() con las particiones del
 * topic en vez de subscribe(), porque seek() solo funciona sobre particiones ya
 * asignadas, y con subscribe() la asignacion ocurre despues, dentro de poll().
 * Los offsets NO se confirman en Kafka (enable.auto.commit=false): la fuente de
 * verdad del progreso es el checkpoint transaccional en la base de datos.
 */
@Component
public class RecuperacionService {

    private static final Logger log = LoggerFactory.getLogger(RecuperacionService.class);

    public static final String MODO_REANUDADO = "REANUDADO_DESDE_CHECKPOINT";
    public static final String MODO_REPRODUCCION = "REPRODUCCION_DESDE_INICIO_DEL_LOG";
    public static final String MODO_MIXTO = "MIXTO";

    private final ProcesadorEntrenamientoService procesador;
    private final String bootstrapServers;
    private final String topic;
    private final String groupId;
    private final long pollMs;
    private final long retryBackoffMs;
    private final boolean habilitado;

    private volatile boolean activo = false;
    private volatile boolean reinicioSolicitado = false;
    private volatile String estado = "SIN_INICIAR";
    private volatile String modoArranque = "N/A";
    private volatile String ultimoError = null;
    private volatile LocalDateTime ultimoProcesado = null;
    private final AtomicLong procesadosDesdeArranque = new AtomicLong();
    private volatile KafkaConsumer<String, String> consumerActual;
    private Thread hilo;

    public RecuperacionService(ProcesadorEntrenamientoService procesador,
                               @Value("${spring.kafka.bootstrap-servers}") String bootstrapServers,
                               @Value("${gym.kafka.entrenamiento-topic}") String topic,
                               @Value("${gym.analytics.recovery.group-id}") String groupId,
                               @Value("${gym.analytics.recovery.poll-ms}") long pollMs,
                               @Value("${gym.analytics.recovery.retry-backoff-ms}") long retryBackoffMs,
                               @Value("${gym.analytics.recovery.enabled}") boolean habilitado) {
        this.procesador = procesador;
        this.bootstrapServers = bootstrapServers;
        this.topic = topic;
        this.groupId = groupId;
        this.pollMs = pollMs;
        this.retryBackoffMs = retryBackoffMs;
        this.habilitado = habilitado;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void iniciar() {
        if (!habilitado) {
            estado = "DESHABILITADO";
            log.info("[recuperacion] Deshabilitado (gym.analytics.recovery.enabled=false).");
            return;
        }
        activo = true;
        hilo = new Thread(this::ejecutar, "kafka-recuperacion");
        hilo.start();
    }

    @PreDestroy
    public void detener() {
        activo = false;
        KafkaConsumer<String, String> consumer = consumerActual;
        if (consumer != null) {
            consumer.wakeup(); // corta el poll() en curso
        }
        if (hilo != null) {
            try {
                hilo.join(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /** Pide re-procesar todo: borra datos y checkpoints y vuelve a leer el log desde el inicio. */
    public void solicitarReinicio() {
        if (!habilitado) {
            throw new IllegalStateException("El proceso de recuperacion esta deshabilitado.");
        }
        log.warn("[recuperacion] Reinicio solicitado: se borraran datos y checkpoints y se reproducira el log.");
        reinicioSolicitado = true;
        KafkaConsumer<String, String> consumer = consumerActual;
        if (consumer != null) {
            consumer.wakeup();
        }
    }

    public RecuperacionEstadoDTO obtenerEstado() {
        return new RecuperacionEstadoDTO(estado, modoArranque, topic, procesadosDesdeArranque.get(),
                procesador.contarRegistros(), ultimoProcesado, ultimoError,
                procesador.listarCheckpoints(topic));
    }

    // ------------------------------------------------------------------ bucle principal

    private void ejecutar() {
        while (activo) {
            try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(propiedadesConsumer())) {
                consumerActual = consumer;
                estado = "INICIANDO";

                List<TopicPartition> particiones = esperarParticiones(consumer);
                if (particiones == null) {
                    break; // se pidio detener mientras se esperaba el topic
                }
                consumer.assign(particiones);
                posicionarDesdeCheckpoint(consumer, particiones);

                estado = "PROCESANDO";
                ultimoError = null;

                while (activo && !reinicioSolicitado) {
                    ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(pollMs));
                    for (ConsumerRecord<String, String> record : records) {
                        // Guarda el dato Y el checkpoint en una sola transaccion.
                        procesador.procesar(record.topic(), record.partition(), record.offset(), record.value());
                        procesadosDesdeArranque.incrementAndGet();
                        ultimoProcesado = LocalDateTime.now();
                    }
                }
            } catch (WakeupException e) {
                // wakeup() lo dispara detener() o solicitarReinicio(); se resuelve abajo.
            } catch (Exception e) {
                estado = "ERROR";
                ultimoError = e.getClass().getSimpleName() + ": " + e.getMessage();
                log.error("[recuperacion] Fallo el procesamiento; se reanudara desde el ultimo checkpoint en {} ms.",
                        retryBackoffMs, e);
                dormir(retryBackoffMs);
            } finally {
                consumerActual = null;
            }

            if (activo && reinicioSolicitado) {
                try {
                    procesador.reiniciarEstado();
                    procesadosDesdeArranque.set(0);
                    log.warn("[recuperacion] Estado borrado; se vuelve a leer el log desde el primer offset.");
                } catch (Exception e) {
                    ultimoError = "No se pudo reiniciar el estado: " + e.getMessage();
                    log.error("[recuperacion] {}", ultimoError, e);
                }
                reinicioSolicitado = false;
            }
        }
        estado = "DETENIDO";
        log.info("[recuperacion] Proceso detenido.");
    }

    /** Espera a que el topic exista (puede arrancar antes que su productor) y devuelve sus particiones. */
    private List<TopicPartition> esperarParticiones(KafkaConsumer<String, String> consumer) {
        while (activo) {
            List<PartitionInfo> infos = consumer.partitionsFor(topic, Duration.ofSeconds(10));
            if (infos != null && !infos.isEmpty()) {
                List<TopicPartition> resultado = new ArrayList<>();
                for (PartitionInfo info : infos) {
                    resultado.add(new TopicPartition(info.topic(), info.partition()));
                }
                return resultado;
            }
            estado = "ESPERANDO_TOPIC";
            dormir(2000);
        }
        return null;
    }

    /**
     * Carga el ultimo offset procesado de cada particion desde la base de datos y se
     * posiciona en el siguiente. Sin checkpoint: primer offset disponible del log.
     */
    private void posicionarDesdeCheckpoint(KafkaConsumer<String, String> consumer, List<TopicPartition> particiones) {
        Map<Integer, Long> checkpoints = procesador.cargarCheckpoints(topic);
        Map<TopicPartition, Long> inicioLog = consumer.beginningOffsets(particiones);

        boolean hayReanudadas = false;
        boolean hayReproducidas = false;

        for (TopicPartition tp : particiones) {
            long primerOffsetDisponible = inicioLog.get(tp);
            Long ultimoProcesado = checkpoints.get(tp.partition());
            long siguiente;

            if (ultimoProcesado == null) {
                siguiente = primerOffsetDisponible;
                hayReproducidas = true;
                log.info("[recuperacion] {} sin checkpoint -> se lee desde el inicio del log (offset {}).",
                        tp, siguiente);
            } else {
                siguiente = ultimoProcesado + 1;
                if (siguiente < primerOffsetDisponible) {
                    log.warn("[recuperacion] {}: el checkpoint ({}) apunta a datos que la retencion ya borro; " +
                            "se arranca en el primer offset disponible ({}). Hay un hueco de datos.",
                            tp, ultimoProcesado, primerOffsetDisponible);
                    siguiente = primerOffsetDisponible;
                }
                hayReanudadas = true;
                log.info("[recuperacion] {} reanuda desde el checkpoint: ultimo procesado {}, siguiente {}.",
                        tp, ultimoProcesado, siguiente);
            }
            consumer.seek(tp, siguiente);
        }

        if (hayReanudadas && hayReproducidas) {
            modoArranque = MODO_MIXTO;
        } else if (hayReanudadas) {
            modoArranque = MODO_REANUDADO;
        } else {
            modoArranque = MODO_REPRODUCCION;
        }
    }

    private Properties propiedadesConsumer() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        // El progreso lo lleva el checkpoint transaccional en la BD, no los commits de Kafka.
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ALLOW_AUTO_CREATE_TOPICS_CONFIG, "false");
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "100");
        return props;
    }

    private void dormir(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            activo = false;
        }
    }
}
