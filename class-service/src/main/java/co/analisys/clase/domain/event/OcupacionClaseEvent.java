package co.analisys.clase.domain.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Evento de streaming (Kafka) para el monitoreo en tiempo real de ocupación
 * de clases (Parte 3.1 del taller). A diferencia de los eventos de RabbitMQ
 * (gym.events, pub/sub de negocio "algo importante pasó"), este es un dato
 * de alta frecuencia pensado para alimentar un dashboard: se publica en el
 * topic "ocupacion-clases", usando el id de la clase como key del mensaje
 * para que todas las actualizaciones de una misma clase caigan siempre en
 * la misma partición y se procesen en orden.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OcupacionClaseEvent {
    private Long claseId;
    private String nombreClase;
    private int ocupacionActual;
    private int capacidadMaxima;
    private LocalDateTime timestamp;
}
