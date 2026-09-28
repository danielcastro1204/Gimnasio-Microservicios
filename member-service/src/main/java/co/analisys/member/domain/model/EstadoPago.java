package co.analisys.member.domain.model;

public enum EstadoPago {
    /** Se creó el registro y se publicó el evento; RabbitMQ todavía no lo procesa. */
    PENDIENTE,
    /** El consumidor de "pagos-queue" procesó el pago sin errores. */
    EXITOSO,
    /** Se agotaron los reintentos (o falló de forma no recuperable) y el mensaje
     *  terminó en "pagos-dlq"; requiere revisión manual. */
    FALLIDO
}
