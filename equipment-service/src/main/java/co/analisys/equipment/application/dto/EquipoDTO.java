package co.analisys.equipment.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EquipoDTO {
    private Long id;

    @NotBlank(message = "El nombre del equipo es obligatorio")
    private String nombre;

    private String descripcion;

    @Min(value = 0, message = "La cantidad no puede ser negativa")
    private int cantidad;
}
