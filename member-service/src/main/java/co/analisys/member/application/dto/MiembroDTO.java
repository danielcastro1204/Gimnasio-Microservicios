package co.analisys.member.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO de transporte para el agregado Miembro.
 * Es el contrato público que consumen los clientes REST y otros microservicios.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MiembroDTO {
    private Long id;

    @NotBlank(message = "El nombre del miembro es obligatorio")
    private String nombre;

    @NotBlank(message = "El email del miembro es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    private String email;

    private LocalDate fechaInscripcion;
}
