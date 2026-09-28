package co.analisys.analytics.infrastructure.controller;

import co.analisys.analytics.application.dto.EntrenamientoRegistradoDTO;
import co.analisys.analytics.application.dto.RecuperacionEstadoDTO;
import co.analisys.analytics.application.service.ProcesadorEntrenamientoService;
import co.analisys.analytics.infrastructure.messaging.RecuperacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Endpoints para observar y demostrar la recuperacion basada en el log de
 * Kafka (Parte 4 del taller).
 */
@Tag(name = "Recuperacion (Kafka)", description = "Checkpoints de offsets y reproduccion del log de datos-entrenamiento")
@RestController
@RequestMapping("/api/analytics/recovery")
public class RecuperacionController {

    private final RecuperacionService recuperacionService;
    private final ProcesadorEntrenamientoService procesador;

    public RecuperacionController(RecuperacionService recuperacionService,
                                  ProcesadorEntrenamientoService procesador) {
        this.recuperacionService = recuperacionService;
        this.procesador = procesador;
    }

    @Operation(summary = "Estado de la recuperacion",
            description = "Estado del proceso, modo de arranque (REANUDADO_DESDE_CHECKPOINT o " +
                    "REPRODUCCION_DESDE_INICIO_DEL_LOG) y ultimo offset procesado por particion.")
    @ApiResponse(responseCode = "200", description = "Estado actual")
    @GetMapping("/status")
    public RecuperacionEstadoDTO estado() {
        return recuperacionService.obtenerEstado();
    }

    @Operation(summary = "Ultimas sesiones reconstruidas desde el log",
            description = "Hasta 50 registros, del mas reciente al mas antiguo, con particion y offset de origen.")
    @ApiResponse(responseCode = "200", description = "Registros procesados")
    @GetMapping("/records")
    public List<EntrenamientoRegistradoDTO> registros() {
        return procesador.ultimosRegistros();
    }

    @Operation(summary = "Simular perdida de estado y reproducir el log",
            description = "Borra los datos y los checkpoints y vuelve a leer datos-entrenamiento desde el primer " +
                    "offset disponible. Sirve para demostrar que el estado se reconstruye desde Kafka.")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Reinicio solicitado"),
            @ApiResponse(responseCode = "403", description = "Solo ADMIN")
    })
    @PostMapping("/reset")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void reiniciar() {
        recuperacionService.solicitarReinicio();
    }
}
