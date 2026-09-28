package co.analisys.notification.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * "Dashboard" de ocupación en tiempo real (Parte 3.1 del taller): un
 * snapshot por clase, sobrescrito en cada mensaje que llega del topic Kafka
 * "ocupacion-clases". No es un histórico; es intencionalmente el último
 * estado conocido de cada clase, que es lo que un dashboard necesita mostrar.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ocupacion_clases")
public class OcupacionClase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private Long claseId;

    private String nombreClase;

    private int ocupacionActual;

    private int capacidadMaxima;

    private LocalDateTime ultimaActualizacion;
}
