package co.analisys.notification.domain.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Copia local (mismo contrato JSON, sin dependencia de código) del evento
 * publicado por class-service en las routing keys "class.created" /
 * "class.updated" / "class.cancelled".
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassScheduleEvent {
    private String tipoEvento;
    private Long id;
    private String nombre;
    private LocalDateTime horario;
    private int capacidadMaxima;
    private Long entrenadorId;
    private LocalDateTime timestamp;
}
