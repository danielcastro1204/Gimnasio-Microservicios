package co.analisys.notification.domain.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Copia local (mismo contrato JSON, sin dependencia de código) del evento
 * que class-service publica en el topic Kafka "ocupacion-clases".
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
