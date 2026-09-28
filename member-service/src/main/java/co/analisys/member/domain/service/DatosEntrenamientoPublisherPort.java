package co.analisys.member.domain.service;

import co.analisys.member.domain.event.DatosEntrenamientoEvent;

/**
 * Puerto de dominio (hexagonal): publicar los datos de una sesion de
 * entrenamiento. El dominio no sabe que viaja por Kafka; eso lo resuelve
 * la infraestructura (ver KafkaDatosEntrenamientoPublisher).
 */
public interface DatosEntrenamientoPublisherPort {
    void publicar(DatosEntrenamientoEvent evento);
}
