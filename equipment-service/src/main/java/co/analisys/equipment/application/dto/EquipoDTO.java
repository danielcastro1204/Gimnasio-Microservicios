package co.analisys.equipment.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EquipoDTO {
    @Schema(description = "Identificador único, generado por el servicio", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Nombre del equipo", example = "Mancuernas")
    @NotBlank(message = "El nombre del equipo es obligatorio")
    private String nombre;

    @Schema(description = "Descripción opcional del equipo", example = "Juego de mancuernas de 2 a 20 kg")
    private String descripcion;

    @Schema(description = "Unidades disponibles en inventario (no puede ser negativa)", example = "10")
    @Min(value = 0, message = "La cantidad no puede ser negativa")
    private int cantidad;
}
