package co.analisys.notification.domain.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Copia local (mismo contrato JSON, sin dependencia de código) del evento
 * publicado por member-service en la routing key "member.registered".
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberRegisteredEvent {
    private Long id;
    private String nombre;
    private String email;
    private LocalDate fechaInscripcion;
    private LocalDateTime timestamp;
}
