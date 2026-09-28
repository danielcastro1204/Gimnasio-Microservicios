package co.analisys.analytics.domain.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Copia local (mismo contrato JSON, sin dependencia de codigo) del evento
 * que member-service publica en el topic Kafka "datos-entrenamiento".
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DatosEntrenamiento {
    private Long miembroId;
    private String ejercicio;
    private int duracionMinutos;
    private int caloriasQuemadas;
    private int frecuenciaCardiacaPromedio;
    private LocalDateTime fecha;
}
