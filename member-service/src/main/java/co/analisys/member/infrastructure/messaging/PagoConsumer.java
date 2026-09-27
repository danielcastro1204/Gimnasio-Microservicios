package co.analisys.member.infrastructure.messaging;

import co.analisys.member.application.service.PagoApplicationService;
import co.analisys.member.domain.event.PagoEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Consumidor de "pagos-queue". No necesita capturar el fallo ni reenviar
 * manualmente a la DLQ (a diferencia del ejemplo literal de la guía del
 * taller, que usa AmqpRejectAndDontRequeueException a la primera): eso ya lo
 * hace el contenedor de este listener (ver el bean
 * "pagosListenerContainerFactory" en RabbitConfig). Si procesarPago lanza una
 * excepción, ese contenedor reintenta hasta 3 veces con backoff exponencial;
 * si se agotan los reintentos, rechaza el mensaje SIN reencolarlo
 * (default-requeue-rejected: false en application.yml) y RabbitMQ lo enruta
 * a "pagos-dlq" gracias a los argumentos x-dead-letter-* declarados en
 * pagosQueue() (ver RabbitConfig).
 */
@Component
public class PagoConsumer {

    private static final Logger log = LoggerFactory.getLogger(PagoConsumer.class);

    private final PagoApplicationService pagoApplicationService;

    public PagoConsumer(PagoApplicationService pagoApplicationService) {
        this.pagoApplicationService = pagoApplicationService;
    }

    @RabbitListener(queues = "pagos-queue", containerFactory = "pagosListenerContainerFactory")
    public void procesarPago(PagoEvent evento) {
        log.info("Procesando pago {} del miembro {}", evento.getPagoId(), evento.getMiembroId());

        if (evento.isSimularFallo()) {
            // Simula, a propósito, una pasarela de pagos que rechaza la transacción.
            // Cada intento fallido queda en el log; tras agotar los reintentos
            // configurados, este mismo log es la evidencia de "número de reintentos"
            // que se puede mostrar en la demo antes de ver el mensaje en pagos-dlq.
            log.warn("Fallo simulado procesando el pago {}", evento.getPagoId());
            throw new PagoFallidoException("Fallo simulado en el procesamiento del pago " + evento.getPagoId());
        }

        pagoApplicationService.marcarExitoso(evento.getPagoId());
        log.info("Pago {} procesado con éxito", evento.getPagoId());
    }
}
