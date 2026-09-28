package co.analisys.analytics.infrastructure.controller;

import co.analisys.analytics.application.service.ResumenEntrenamientoQueryService;
import co.analisys.analytics.domain.event.ResumenEntrenamiento;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Resultado del stream processor (Parte 3.3): resumen de entrenamiento por
 * miembro en ventanas de 7 dias, leido del state store de Kafka Streams.
 */
@Tag(name = "Resumen de entrenamiento (Kafka Streams)", description = "Agregaciones semanales por miembro calculadas en streaming sobre el topic datos-entrenamiento")
@RestController
@RequestMapping("/api/analytics/training")
public class ResumenEntrenamientoController {

    private final ResumenEntrenamientoQueryService queryService;

    public ResumenEntrenamientoController(ResumenEntrenamientoQueryService queryService) {
        this.queryService = queryService;
    }

    @Operation(summary = "Resumen de la semana actual de un miembro",
            description = "Total de sesiones, minutos y calorias, frecuencia cardiaca promedio y maxima de la ventana mas reciente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resumen encontrado"),
            @ApiResponse(responseCode = "404", description = "Aun no hay datos procesados para ese miembro"),
            @ApiResponse(responseCode = "503", description = "Kafka Streams esta arrancando o rebalanceando")
    })
    @GetMapping("/{miembroId}/summary")
    public ResumenEntrenamiento resumenActual(
            @Parameter(description = "Id del miembro", example = "1") @PathVariable Long miembroId) {
        return queryService.obtenerResumenActual(miembroId);
    }

    @Operation(summary = "Historial de resumenes semanales de un miembro",
            description = "Un resumen por cada ventana de 7 dias con datos, de la mas antigua a la mas reciente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Historial encontrado"),
            @ApiResponse(responseCode = "404", description = "Aun no hay datos procesados para ese miembro"),
            @ApiResponse(responseCode = "503", description = "Kafka Streams esta arrancando o rebalanceando")
    })
    @GetMapping("/{miembroId}/history")
    public List<ResumenEntrenamiento> historial(
            @Parameter(description = "Id del miembro", example = "1") @PathVariable Long miembroId) {
        return queryService.obtenerHistorial(miembroId);
    }
}
