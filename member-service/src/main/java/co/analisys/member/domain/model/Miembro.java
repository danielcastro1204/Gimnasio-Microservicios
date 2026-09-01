package co.analisys.member.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "miembros")
public class Miembro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del miembro es obligatorio")
    private String nombre;

    @NotBlank(message = "El email del miembro es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    private String email;

    private LocalDate fechaInscripcion;

    @PrePersist
    public void prePersist() {
        if (this.fechaInscripcion == null) {
            this.fechaInscripcion = LocalDate.now();
        }
    }
}
