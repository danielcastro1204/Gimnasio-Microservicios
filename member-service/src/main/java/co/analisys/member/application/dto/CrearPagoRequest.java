package co.analisys.member.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrearPagoRequest {

    @Schema(description = "Monto a cobrar", example = "150000")
    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor que cero")
    private BigDecimal monto;

    @Schema(description = "Solo para pruebas/demo: si es true, el consumidor simula que la " +
            "pasarela de pagos falla, para poder mostrar cómo el mensaje termina en pagos-dlq " +
            "tras agotar los reintentos.", example = "false")
    private boolean simularFallo;
}
