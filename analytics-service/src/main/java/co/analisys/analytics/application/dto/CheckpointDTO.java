package co.analisys.analytics.application.dto;

import java.time.LocalDateTime;

public record CheckpointDTO(int particion, long ultimoOffset, LocalDateTime actualizado) {
}
