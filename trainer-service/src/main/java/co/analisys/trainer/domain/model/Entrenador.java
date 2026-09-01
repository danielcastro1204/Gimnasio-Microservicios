package co.analisys.trainer.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Aggregate root del Bounded Context "Coaching".
 * Representa a un entrenador del gimnasio y su especialidad.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "entrenadores")
public class Entrenador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del entrenador es obligatorio")
    private String nombre;

    @NotBlank(message = "La especialidad del entrenador es obligatoria")
    private String especialidad;
}
