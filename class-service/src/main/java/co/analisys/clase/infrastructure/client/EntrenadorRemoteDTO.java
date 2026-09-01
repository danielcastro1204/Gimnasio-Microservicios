package co.analisys.clase.infrastructure.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO usado únicamente para deserializar la respuesta JSON de trainer-service.
 * Es distinto del DTO público de class-service para no acoplar ambos contratos.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntrenadorRemoteDTO {
    private Long id;
    private String nombre;
    private String especialidad;
}
