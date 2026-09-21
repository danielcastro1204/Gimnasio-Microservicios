package co.analisys.member.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Formato estándar de error de la API (el que arma GlobalExceptionHandler).
 * Esta clase existe solo para documentar el cuerpo de error en Swagger: el handler
 * construye la respuesta con un Map que tiene estas mismas claves.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Formato estándar de error de la API")
public class ApiErrorResponse {

    @Schema(description = "Momento en que ocurrió el error", example = "2026-09-21T10:15:30")
    private LocalDateTime timestamp;

    @Schema(description = "Código de estado HTTP", example = "409")
    private int status;

    @Schema(description = "Descripción corta del estado HTTP", example = "Conflict")
    private String error;

    @Schema(description = "Detalle legible del error", example = "Ya existe un miembro registrado con el email: ana.perez@gimnasio.com")
    private String message;
}
