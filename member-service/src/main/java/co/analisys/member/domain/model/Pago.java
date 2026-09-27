package co.analisys.member.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Un pago de membresía. Se crea en estado PENDIENTE dentro de la misma
 * transacción HTTP; el resultado real (EXITOSO/FALLIDO) lo decide, en otro
 * momento, el consumidor de RabbitMQ ("pagos-queue" / "pagos-dlq"). Por eso
 * "detalle" existe: para dejar registrado por qué terminó fallido.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pagos")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long miembroId;

    private BigDecimal monto;

    @Enumerated(EnumType.STRING)
    private EstadoPago estado;

    private LocalDateTime fechaCreacion;

    private LocalDateTime fechaProcesado;

    /** Motivo del fallo cuando estado = FALLIDO; null en cualquier otro caso. */
    private String detalle;

    @PrePersist
    public void prePersist() {
        if (this.fechaCreacion == null) {
            this.fechaCreacion = LocalDateTime.now();
        }
        if (this.estado == null) {
            this.estado = EstadoPago.PENDIENTE;
        }
    }
}
