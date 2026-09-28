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
 * Checkpoint de progreso (Parte 4 del taller): ultimo offset YA procesado de
 * cada particion de un topic. Se guarda en la misma transaccion que el
 * resultado del procesamiento, asi nunca queda un offset "adelantado" a los
 * datos realmente guardados (ni al reves).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "kafka_checkpoints",
        uniqueConstraints = @UniqueConstraint(columnNames = {"topic", "particion"}))
public class KafkaCheckpoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String topic;

    @Column(nullable = false)
    private int particion;

    /** Ultimo offset procesado; al reanudar se lee desde ultimoOffset + 1. */
    @Column(nullable = false)
    private long ultimoOffset;

    private LocalDateTime actualizado;
}
