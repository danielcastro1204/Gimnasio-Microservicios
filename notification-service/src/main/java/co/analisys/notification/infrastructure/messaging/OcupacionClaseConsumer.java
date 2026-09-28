package co.analisys.notification.infrastructure.messaging;

import co.analisys.notification.application.service.OcupacionClaseApplicationService;
import co.analisys.notification.domain.event.OcupacionClaseEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Consumidor del topic "ocupacion-clases" (Parte 3.1 del taller): cada
 * actualización que publica class-service llega aquí y se usa para
 * mantener al día el "dashboard" de ocupación (ver OcupacionClaseController).
 * El groupId "monitoreo-grupo" coincide con el que pide la guía del taller.
 */
@Component
public class OcupacionClaseConsumer {

    private final OcupacionClaseApplicationService ocupacionClaseApplicationService;

    public OcupacionClaseConsumer(OcupacionClaseApplicationService ocupacionClaseApplicationService) {
        this.ocupacionClaseApplicationService = ocupacionClaseApplicationService;
    }

    @KafkaListener(topics = "${gym.kafka.ocupacion-topic}", groupId = "monitoreo-grupo")
    public void consumirActualizacionOcupacion(OcupacionClaseEvent ocupacion) {
        ocupacionClaseApplicationService.actualizarDashboard(ocupacion);
    }
}
