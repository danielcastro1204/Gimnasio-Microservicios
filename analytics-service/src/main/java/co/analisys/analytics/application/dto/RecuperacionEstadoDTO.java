package co.analisys.analytics.application.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Foto del proceso de recuperacion: en que estado esta, como arranco
 * (reanudando desde un checkpoint o reprocesando todo el log) y hasta que
 * offset ha llegado en cada particion.
 */
public record RecuperacionEstadoDTO(String estado,
                                    String modoArranque,
                                    String topic,
                                    long procesadosDesdeArranque,
                                    long totalRegistrosGuardados,
                                    LocalDateTime ultimoProcesado,
                                    String ultimoError,
                                    List<CheckpointDTO> checkpoints) {
}
