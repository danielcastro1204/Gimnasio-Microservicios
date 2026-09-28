package co.analisys.notification.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OcupacionClaseDTO {
    private Long claseId;
    private String nombreClase;
    private int ocupacionActual;
    private int capacidadMaxima;
    private LocalDateTime ultimaActualizacion;
}
