package co.analisys.member.domain.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Evento de streaming (Kafka) con los datos de UNA sesion de entrenamiento
 * de un miembro (Parte 3.3 del taller). Se publica en el topic
 * "datos-entrenamiento" con key = miembroId; analytics-service lo agrega
 * por miembro en ventanas de 7 dias con Kafka Streams.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DatosEntrenamientoEvent {
    private Long miembroId;
    private String ejercicio;
    private int duracionMinutos;
    private int caloriasQuemadas;
    private int frecuenciaCardiacaPromedio;
    private LocalDateTime fecha;
}
