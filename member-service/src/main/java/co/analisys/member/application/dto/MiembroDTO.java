package co.analisys.member.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "Identificador único, generado por el servicio", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Nombre completo del miembro", example = "Ana Pérez")
    @NotBlank(message = "El nombre del miembro es obligatorio")
    private String nombre;

    @Schema(description = "Email del miembro (debe ser único en el sistema)", example = "ana.perez@gimnasio.com")
    @NotBlank(message = "El email del miembro es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    private String email;

    @Schema(description = "Fecha de inscripción (formato yyyy-MM-dd)", example = "2026-01-15")
    private LocalDate fechaInscripcion;
}
