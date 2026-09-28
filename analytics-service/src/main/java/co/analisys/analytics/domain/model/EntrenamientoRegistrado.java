package co.analisys.analytics.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Historial de sesiones de entrenamiento reconstruido a partir del log de
 * Kafka. Es el "estado" que el proceso de recuperacion puede volver a
 * construir desde cero releyendo el topic. La restriccion unica
 * (topic, particion, offset) hace el procesamiento idempotente: si un mensaje
 * se re-entrega, no se duplica.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "entrenamientos_registrados",
        uniqueConstraints = @UniqueConstraint(columnNames = {"topic", "particion", "offsetKafka"}))
public class EntrenamientoRegistrado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String topic;

    @Column(nullable = false)
    private int particion;

    @Column(nullable = false)
    private long offsetKafka;

    private Long miembroId;
    private String ejercicio;
    private int duracionMinutos;
    private int caloriasQuemadas;
    private int frecuenciaCardiacaPromedio;
    private LocalDateTime fechaEntrenamiento;
    private LocalDateTime procesadoEn;
}
