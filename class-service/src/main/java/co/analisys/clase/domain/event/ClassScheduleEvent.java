package co.analisys.clase.domain.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Evento de dominio publicado ante un cambio de horario relevante
 * (creación, actualización o cancelación de una clase). Es el contrato
 * (payload JSON) que consume notification-service.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassScheduleEvent {
    private TipoEventoClase tipoEvento;
    private Long id;
    private String nombre;
    private LocalDateTime horario;
    private int capacidadMaxima;
    private Long entrenadorId;
    private LocalDateTime timestamp;
}
