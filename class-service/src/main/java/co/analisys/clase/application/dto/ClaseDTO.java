package co.analisys.clase.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de entrada/salida principal para el recurso Clase.
 * El campo entrenador (EntrenadorInfoDTO) se completa solo en lectura,
 * enriquecido vía REST; en escritura solo se exige entrenadorId.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClaseDTO {
    @Schema(description = "Identificador único, generado por el servicio", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Nombre de la clase", example = "Yoga Matutino")
    @NotBlank(message = "El nombre de la clase es obligatorio")
    private String nombre;

    @Schema(description = "Fecha y hora de la clase (ISO-8601, sin zona horaria)", example = "2026-10-05T18:00:00")
    @NotNull(message = "El horario de la clase es obligatorio")
    private LocalDateTime horario;

    @Schema(description = "Cupo máximo de asistentes (mínimo 1)", example = "15")
    @Min(value = 1, message = "La capacidad máxima debe ser mayor a 0")
    private int capacidadMaxima;

    @Schema(description = "Id del entrenador que dicta la clase. Debe existir en trainer-service", example = "1")
    @NotNull(message = "El id del entrenador es obligatorio")
    private Long entrenadorId;

    /** Enriquecido vía REST desde trainer-service; puede venir null si el servicio no respondió. */
    @Schema(description = "Datos del entrenador, obtenidos de trainer-service solo en lecturas. Puede venir null si ese servicio no responde", accessMode = Schema.AccessMode.READ_ONLY)
    private EntrenadorInfoDTO entrenador;

    public ClaseDTO(Long id, String nombre, LocalDateTime horario, int capacidadMaxima, Long entrenadorId) {
        this.id = id;
        this.nombre = nombre;
        this.horario = horario;
        this.capacidadMaxima = capacidadMaxima;
        this.entrenadorId = entrenadorId;
    }
}
