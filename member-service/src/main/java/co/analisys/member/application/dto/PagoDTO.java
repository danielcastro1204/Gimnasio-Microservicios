package co.analisys.member.application.dto;

import co.analisys.member.domain.model.EstadoPago;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Estado de un pago. Se crea PENDIENTE y cambia de forma asíncrona " +
        "(vía RabbitMQ) a EXITOSO o FALLIDO; conviene volver a consultar GET /api/members/payments/{id} " +
        "un momento después de crearlo para ver el resultado final.")
public class PagoDTO {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, example = "1")
    private Long id;

    @Schema(example = "1")
    private Long miembroId;

    @Schema(example = "150000")
    private BigDecimal monto;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private EstadoPago estado;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime fechaCreacion;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime fechaProcesado;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Motivo del fallo, solo si estado = FALLIDO")
    private String detalle;
}
