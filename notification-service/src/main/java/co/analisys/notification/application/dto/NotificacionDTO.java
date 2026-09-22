package co.analisys.notification.application.dto;

import co.analisys.notification.domain.model.TipoNotificacion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificacionDTO {
    private Long id;
    private TipoNotificacion tipo;
    private String destinatario;
    private String mensaje;
    private String origenEvento;
    private LocalDateTime fechaEnvio;
}
