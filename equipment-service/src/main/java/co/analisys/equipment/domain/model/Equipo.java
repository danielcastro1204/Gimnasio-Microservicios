package co.analisys.equipment.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Aggregate root del Bounded Context "Inventory".
 * Representa un tipo de equipo disponible en el inventario del gimnasio.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "equipos")
public class Equipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del equipo es obligatorio")
    private String nombre;

    private String descripcion;

    @Min(value = 0, message = "La cantidad no puede ser negativa")
    private int cantidad;
}
