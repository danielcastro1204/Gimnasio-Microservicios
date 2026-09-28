package co.analisys.clase.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarOcupacionRequest {

    @Schema(description = "Nueva cantidad de asistentes registrados en la clase", example = "8")
    @NotNull(message = "La ocupación actual es obligatoria")
    @Min(value = 0, message = "La ocupación actual no puede ser negativa")
    private Integer ocupacionActual;
}
