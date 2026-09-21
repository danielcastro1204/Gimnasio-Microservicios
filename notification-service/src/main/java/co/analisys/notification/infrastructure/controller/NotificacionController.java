package co.analisys.notification.infrastructure.controller;

import co.analisys.notification.application.dto.NotificacionDTO;
import co.analisys.notification.application.service.NotificacionApplicationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * API de solo lectura para verificar qué notificaciones asíncronas se han
 * procesado (útil para demostrar el flujo pub/sub sin depender solo de logs).
 */
@RestController
@RequestMapping("/api/notifications")
public class NotificacionController {

    private final NotificacionApplicationService notificacionApplicationService;

    public NotificacionController(NotificacionApplicationService notificacionApplicationService) {
        this.notificacionApplicationService = notificacionApplicationService;
    }

    @GetMapping
    public List<NotificacionDTO> obtenerTodas() {
        return notificacionApplicationService.obtenerTodas();
    }

    @GetMapping("/{id}")
    public NotificacionDTO obtenerPorId(@PathVariable Long id) {
        return notificacionApplicationService.obtenerPorId(id);
    }
}
