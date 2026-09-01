package co.analisys.clase.application.dto;

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
    private Long id;

    @NotBlank(message = "El nombre de la clase es obligatorio")
    private String nombre;

    @NotNull(message = "El horario de la clase es obligatorio")
    private LocalDateTime horario;

    @Min(value = 1, message = "La capacidad máxima debe ser mayor a 0")
    private int capacidadMaxima;

    @NotNull(message = "El id del entrenador es obligatorio")
    private Long entrenadorId;

    /** Enriquecido vía REST desde trainer-service; puede venir null si el servicio no respondió. */
    private EntrenadorInfoDTO entrenador;

    public ClaseDTO(Long id, String nombre, LocalDateTime horario, int capacidadMaxima, Long entrenadorId) {
        this.id = id;
        this.nombre = nombre;
        this.horario = horario;
        this.capacidadMaxima = capacidadMaxima;
        this.entrenadorId = entrenadorId;
    }
}
