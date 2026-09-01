package co.analisys.clase.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Aggregate root del Bounded Context "Scheduling".
 * Ya NO tiene una relación JPA (@ManyToOne) hacia Entrenador, porque esa entidad
 * ahora vive en otro microservicio con su propia base de datos.
 * En su lugar se guarda una referencia por identidad (entrenadorId), y la
 * verificación/enriquecimiento de esa referencia se hace vía REST (ver TrainerClient).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "clases")
public class Clase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre de la clase es obligatorio")
    private String nombre;

    @NotNull(message = "El horario de la clase es obligatorio")
    private LocalDateTime horario;

    @Min(value = 1, message = "La capacidad máxima debe ser mayor a 0")
    private int capacidadMaxima;

    /**
     * Referencia por identidad al entrenador (dueño: trainer-service).
     * Se evita compartir la entidad JPA Entrenador entre microservicios.
     */
    @NotNull(message = "El id del entrenador es obligatorio")
    private Long entrenadorId;
}
