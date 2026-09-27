package co.analisys.member.infrastructure.messaging;

/**
 * Señala que el procesamiento de un pago falló. Se deja como RuntimeException
 * "normal" (no AmqpRejectAndDontRequeueException) a propósito: así Spring
 * Retry sí la reintenta; solo tras agotar los reintentos el contenedor la
 * trata como rechazo definitivo y el mensaje va a la DLQ.
 */
public class PagoFallidoException extends RuntimeException {
    public PagoFallidoException(String message) {
        super(message);
    }
}
