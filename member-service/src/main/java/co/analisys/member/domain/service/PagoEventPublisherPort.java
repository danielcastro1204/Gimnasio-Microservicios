package co.analisys.member.domain.service;

/**
 * Puerto de dominio (hexagonal): publicar un pago recién creado para que se
 * procese de forma asíncrona. El dominio no sabe que esto viaja por
 * RabbitMQ ni que existe una "pagos-dlq"; eso lo resuelve la infraestructura
 * (ver RabbitPagoEventPublisher).
 */
public interface PagoEventPublisherPort {
    void publicarPagoPendiente(Long pagoId, Long miembroId, boolean simularFallo);
}
