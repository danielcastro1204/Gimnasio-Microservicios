package co.analisys.member.infrastructure.controller;

import co.analisys.member.application.dto.ApiErrorResponse;
import co.analisys.member.application.dto.RegistrarEntrenamientoRequest;
import co.analisys.member.application.service.EntrenamientoApplicationService;
import co.analisys.member.domain.event.DatosEntrenamientoEvent;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * Entrada de datos de entrenamiento. Responde 202 Accepted: el dato se
 * publica en el topic Kafka "datos-entrenamiento" y se analiza despues,
 * de forma asincrona, en analytics-service.
 */
@Tag(name = "Entrenamientos (Kafka)", description = "Registro de sesiones de entrenamiento que alimentan el analisis en streaming (topic datos-entrenamiento)")
@RestController
@RequestMapping("/api/members")
public class EntrenamientoController {

    private final EntrenamientoApplicationService entrenamientoApplicationService;

    public EntrenamientoController(EntrenamientoApplicationService entrenamientoApplicationService) {
        this.entrenamientoApplicationService = entrenamientoApplicationService;
    }

    @Operation(summary = "Registrar una sesion de entrenamiento de un miembro",
            description = "Publica el dato en el topic \"datos-entrenamiento\" (key = id del miembro). " +
                    "El resumen semanal se consulta en analytics-service: GET /api/analytics/training/{miembroId}/summary.")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Dato aceptado y publicado en Kafka",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DatosEntrenamientoEvent.class))),
            @ApiResponse(responseCode = "400", description = "Datos invalidos",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "No existe un miembro con ese id",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping("/{miembroId}/training-data")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public DatosEntrenamientoEvent registrarEntrenamiento(
            @Parameter(description = "Id del miembro que entreno", example = "1") @PathVariable Long miembroId,
            @Valid @RequestBody RegistrarEntrenamientoRequest request) {
        return entrenamientoApplicationService.registrar(miembroId, request);
    }
}
