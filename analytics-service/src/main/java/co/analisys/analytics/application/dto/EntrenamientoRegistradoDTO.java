package co.analisys.analytics.application.dto;

import java.time.LocalDateTime;

public record EntrenamientoRegistradoDTO(int particion, long offset, Long miembroId, String ejercicio,
                                         int duracionMinutos, int caloriasQuemadas,
                                         LocalDateTime fechaEntrenamiento, LocalDateTime procesadoEn) {
}
