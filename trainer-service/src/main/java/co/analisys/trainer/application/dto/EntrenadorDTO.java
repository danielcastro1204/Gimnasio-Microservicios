package co.analisys.trainer.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntrenadorDTO {
    @Schema(description = "Identificador único, generado por el servicio", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Nombre completo del entrenador", example = "Carlos Gómez")
    @NotBlank(message = "El nombre del entrenador es obligatorio")
    private String nombre;

    @Schema(description = "Especialidad del entrenador", example = "Yoga")
    @NotBlank(message = "La especialidad del entrenador es obligatoria")
    private String especialidad;
}
