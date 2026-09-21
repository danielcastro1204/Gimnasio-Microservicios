package co.analisys.clase.domain.service;

import co.analisys.clase.domain.model.Clase;

/**
 * Puerto de dominio (hexagonal): publicar cambios de horario relevantes.
 * El dominio no sabe que esto viaja por RabbitMQ; eso lo resuelve la
 * infraestructura (ver RabbitClaseEventPublisher).
 */
public interface ClaseEventPublisherPort {
    void publicarCreacion(Clase clase);
    void publicarActualizacion(Clase clase);
    void publicarCancelacion(Clase clase);
}
