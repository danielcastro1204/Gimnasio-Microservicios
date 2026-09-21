package co.analisys.member.domain.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Evento de dominio publicado cuando un nuevo miembro se registra.
 * Es el contrato (payload JSON) que consume notification-service; no se
 * comparte como dependencia de código entre microservicios, solo la forma.
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
