package co.analisys.clase.application.dto;

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
    private Long id;
    private String nombre;
    private String especialidad;
}
