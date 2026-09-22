package co.analisys.notification.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Registro de una notificación asíncrona ya procesada: el "envío" real
 * (email/SMS) está simulado (se registra y se loguea), pero queda persistido
 * para poder demostrar/verificar que el consumo del evento ocurrió.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "notificaciones")
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TipoNotificacion tipo;

    private String destinatario;

    private String mensaje;

    /** Routing key del evento que originó esta notificación (member.registered, class.created, ...). */
    private String origenEvento;

    private LocalDateTime fechaEnvio;
}
