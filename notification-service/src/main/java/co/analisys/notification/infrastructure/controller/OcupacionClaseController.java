package co.analisys.notification.infrastructure.controller;

import co.analisys.notification.application.dto.OcupacionClaseDTO;
import co.analisys.notification.application.service.OcupacionClaseApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * "Dashboard" de ocupación en tiempo real: lectura del último estado
 * conocido de cada clase, alimentado por OcupacionClaseConsumer desde el
 * topic Kafka "ocupacion-clases". Útil para demostrar el flujo de streaming
 * sin depender solo de los logs.
 */
@Tag(name = "Ocupación (Kafka)", description = "Dashboard de ocupación de clases en tiempo real, vía Kafka")
@RestController
@RequestMapping("/api/notifications/occupancy")
public class OcupacionClaseController {

    private final OcupacionClaseApplicationService ocupacionClaseApplicationService;

    public OcupacionClaseController(OcupacionClaseApplicationService ocupacionClaseApplicationService) {
        this.ocupacionClaseApplicationService = ocupacionClaseApplicationService;
    }

    @Operation(summary = "Ver el último estado de ocupación de todas las clases")
    @GetMapping
    public List<OcupacionClaseDTO> obtenerTodas() {
        return ocupacionClaseApplicationService.obtenerTodas();
    }

    @Operation(summary = "Ver el último estado de ocupación de una clase")
    @GetMapping("/{claseId}")
    public OcupacionClaseDTO obtenerPorClaseId(
            @Parameter(description = "Id de la clase", example = "1") @PathVariable Long claseId) {
        return ocupacionClaseApplicationService.obtenerPorClaseId(claseId);
    }
}
