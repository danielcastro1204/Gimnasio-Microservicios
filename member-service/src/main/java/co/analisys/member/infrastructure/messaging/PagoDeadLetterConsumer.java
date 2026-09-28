package co.analisys.member.infrastructure.messaging;

import co.analisys.member.application.service.PagoApplicationService;
import co.analisys.member.domain.event.PagoEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Consumidor de "pagos-dlq". Un mensaje solo llega aquí después de que
 * PagoConsumer agotó sus reintentos sobre "pagos-queue" (ver RabbitConfig y
 * application.yml). En un sistema real, este es el punto donde se alertaría
 * al equipo de soporte o se reintentaría manualmente el cobro; aquí se deja
 * registrado el pago como FALLIDO para que quede visible por
 * GET /api/members/payments/{id}.
 */
@Component
public class PagoDeadLetterConsumer {

    private static final Logger log = LoggerFactory.getLogger(PagoDeadLetterConsumer.class);

    private final PagoApplicationService pagoApplicationService;

    public PagoDeadLetterConsumer(PagoApplicationService pagoApplicationService) {
        this.pagoApplicationService = pagoApplicationService;
    }

    @RabbitListener(queues = "pagos-dlq")
    public void procesarPagoFallido(PagoEvent evento) {
        log.error("Pago {} del miembro {} agotó los reintentos y cayó en pagos-dlq. Requiere revisión manual.",
                evento.getPagoId(), evento.getMiembroId());
        pagoApplicationService.marcarFallido(evento.getPagoId(),
                "Se agotaron los reintentos de procesamiento; revisar manualmente.");
    }
}
