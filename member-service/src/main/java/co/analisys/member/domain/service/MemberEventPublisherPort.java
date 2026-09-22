package co.analisys.member.domain.service;

import co.analisys.member.domain.model.Miembro;

/**
 * Puerto de dominio (hexagonal): publicar que un miembro se registró.
 * El dominio no sabe que la notificación viaja por RabbitMQ; eso lo
 * resuelve la infraestructura (ver RabbitMemberEventPublisher).
 */
public interface MemberEventPublisherPort {
    void publicarRegistro(Miembro miembro);
}
