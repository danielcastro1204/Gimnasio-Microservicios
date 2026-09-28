package co.analisys.member.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrarEntrenamientoRequest {

    @Schema(description = "Ejercicio o actividad realizada", example = "Spinning")
    @NotBlank(message = "El ejercicio es obligatorio")
    private String ejercicio;

    @Schema(description = "Duracion de la sesion en minutos", example = "45")
    @Min(value = 1, message = "La duracion debe ser de al menos 1 minuto")
    @Max(value = 600, message = "La duracion no puede superar 600 minutos")
    private int duracionMinutos;

    @Schema(description = "Calorias quemadas en la sesion", example = "420")
    @Min(value = 0, message = "Las calorias no pueden ser negativas")
    private int caloriasQuemadas;

    @Schema(description = "Frecuencia cardiaca promedio (latidos por minuto)", example = "138")
    @Min(value = 30, message = "La frecuencia cardiaca debe ser al menos 30")
    @Max(value = 250, message = "La frecuencia cardiaca no puede superar 250")
    private int frecuenciaCardiacaPromedio;

    @Schema(description = "Fecha y hora de la sesion. Si se omite se usa el momento actual.",
            example = "2026-09-28T18:30:00")
    private LocalDateTime fecha;
}
