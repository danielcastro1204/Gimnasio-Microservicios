package co.analisys.clase.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representación (posiblemente parcial) del entrenador, obtenida vía REST
 * de trainer-service, para enriquecer la respuesta de una clase.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntrenadorInfoDTO {
    @Schema(description = "Id del entrenador", example = "1")
    private Long id;
    @Schema(description = "Nombre del entrenador", example = "Carlos Gómez")
    private String nombre;
    @Schema(description = "Especialidad del entrenador", example = "Yoga")
    private String especialidad;
}
