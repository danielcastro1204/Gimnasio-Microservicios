package co.analisys.clase.infrastructure.controller;

import co.analisys.clase.application.dto.ApiErrorResponse;
import co.analisys.clase.application.dto.ClaseDTO;
import co.analisys.clase.application.service.ClaseApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * API REST del microservicio class-service.
 * Al programar una clase, este microservicio valida vía REST contra
 * trainer-service que el entrenador exista (GET /api/trainers/{id}/exists).
 */
@Tag(name = "Clases", description = "Programación y consulta de clases (bounded context Scheduling)")
@RestController
@RequestMapping("/api/classes")
public class ClaseController {

    private final ClaseApplicationService claseApplicationService;

    public ClaseController(ClaseApplicationService claseApplicationService) {
        this.claseApplicationService = claseApplicationService;
    }

    @Operation(summary = "Programar una clase",
            description = "Crea una clase nueva. Antes de guardarla, consulta por REST a trainer-service "
                    + "para verificar que el entrenador (entrenadorId) exista. "
                    + "Requiere que trainer-service esté levantado.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Clase programada correctamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ClaseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos (nombre vacío, horario nulo, capacidad menor a 1 o entrenadorId nulo)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Regla de negocio incumplida: el entrenador no existe",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "503", description = "trainer-service no está disponible, no se pudo verificar el entrenador",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClaseDTO programarClase(@Valid @RequestBody ClaseDTO clase) {
        return claseApplicationService.programarClase(clase);
    }

    @Operation(summary = "Listar todas las clases",
            description = "Cada clase se enriquece con los datos del entrenador consultados a trainer-service. "
                    + "Si ese servicio no responde, la clase se devuelve igual pero con el campo entrenador en null.")
    @ApiResponse(responseCode = "200", description = "Lista de clases (puede estar vacía)",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = ClaseDTO.class))))
    @GetMapping
    public List<ClaseDTO> obtenerTodasClases() {
        return claseApplicationService.obtenerTodasClases();
    }

    @Operation(summary = "Obtener una clase por id",
            description = "Incluye los datos del entrenador si trainer-service está disponible.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Clase encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ClaseDTO.class))),
            @ApiResponse(responseCode = "404", description = "No existe una clase con ese id",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ClaseDTO obtenerClasePorId(
            @Parameter(description = "Id de la clase", example = "1") @PathVariable Long id) {
        return claseApplicationService.obtenerClasePorId(id);
    }

    @PutMapping("/{id}")
    public ClaseDTO actualizarClase(@PathVariable Long id, @Valid @RequestBody ClaseDTO clase) {
        return claseApplicationService.actualizarClase(id, clase);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelarClase(@PathVariable Long id) {
        claseApplicationService.cancelarClase(id);
    }
}
