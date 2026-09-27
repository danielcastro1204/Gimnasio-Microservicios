package co.analisys.member.domain.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Payload publicado en "pagos-queue" (y, si falla, reenviado por RabbitMQ a
 * "pagos-dlq" gracias a los argumentos x-dead-letter-* de la cola). No lleva
 * el monto: el consumidor relee el Pago por id, así siempre procesa el
 * estado más reciente en base de datos en vez de una copia potencialmente
 * vieja del mensaje.
 *
 * "simularFallo" es un flag exclusivamente de demo/pruebas: permite forzar a
 * propósito el camino de error para poder mostrar en vivo cómo un pago
 * termina en la DLQ después de agotar los reintentos.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagoEvent {
    private Long pagoId;
    private Long miembroId;
    private boolean simularFallo;
}
