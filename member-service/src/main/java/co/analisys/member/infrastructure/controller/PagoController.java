package co.analisys.member.infrastructure.controller;

import co.analisys.member.application.dto.ApiErrorResponse;
import co.analisys.member.application.dto.CrearPagoRequest;
import co.analisys.member.application.dto.PagoDTO;
import co.analisys.member.application.service.PagoApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * API REST de pagos. El registro es asíncrono: POST devuelve 202 Accepted
 * con el pago en estado PENDIENTE; el resultado real (EXITOSO/FALLIDO) se
 * decide después, vía RabbitMQ ("pagos-queue" / "pagos-dlq"), y se consulta
 * volviendo a pedir GET /api/members/payments/{id}.
 */
@Tag(name = "Pagos", description = "Registro de pagos de membresía y su procesamiento asíncrono vía RabbitMQ (pagos-queue / pagos-dlq)")
@RestController
@RequestMapping("/api/members")
public class PagoController {

    private final PagoApplicationService pagoApplicationService;

    public PagoController(PagoApplicationService pagoApplicationService) {
        this.pagoApplicationService = pagoApplicationService;
    }

    @Operation(summary = "Registrar un pago para un miembro",
            description = "Crea el pago en estado PENDIENTE y publica el evento en \"pagos-queue\". " +
                    "El campo simularFallo=true es solo para demostrar el flujo de la Dead Letter Queue.")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Pago aceptado y encolado para su procesamiento",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PagoDTO.class))),
            @ApiResponse(responseCode = "404", description = "No existe un miembro con ese id",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping("/{miembroId}/payments")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public PagoDTO registrarPago(
            @Parameter(description = "Id del miembro que paga", example = "1") @PathVariable Long miembroId,
            @Valid @RequestBody CrearPagoRequest request) {
        return pagoApplicationService.registrarPago(miembroId, request);
    }

    @Operation(summary = "Listar todos los pagos (cualquier estado)")
    @ApiResponse(responseCode = "200", description = "Lista de pagos",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = PagoDTO.class))))
    @GetMapping("/payments")
    public List<PagoDTO> obtenerTodosPagos() {
        return pagoApplicationService.obtenerTodosPagos();
    }

    @Operation(summary = "Consultar un pago por id",
            description = "Útil para ver, después de unos segundos, si un pago quedó EXITOSO o cayó en FALLIDO tras pasar por la DLQ.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pago encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PagoDTO.class))),
            @ApiResponse(responseCode = "404", description = "No existe un pago con ese id",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/payments/{id}")
    public PagoDTO obtenerPagoPorId(
            @Parameter(description = "Id del pago", example = "1") @PathVariable Long id) {
        return pagoApplicationService.obtenerPagoPorId(id);
    }
}
