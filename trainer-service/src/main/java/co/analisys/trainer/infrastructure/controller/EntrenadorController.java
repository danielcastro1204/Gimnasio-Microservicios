package co.analisys.trainer.infrastructure.controller;

import co.analisys.trainer.application.dto.ApiErrorResponse;
import co.analisys.trainer.application.dto.EntrenadorDTO;
import co.analisys.trainer.application.service.EntrenadorApplicationService;
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
 * API REST del microservicio trainer-service.
 * Expone también GET /api/trainers/{id}/exists, usado por class-service
 * para verificar la existencia de un entrenador sin acoplarse a su base de datos.
 */
@Tag(name = "Entrenadores", description = "Alta y consulta de entrenadores (bounded context Coaching)")
@RestController
@RequestMapping("/api/trainers")
public class EntrenadorController {

    private final EntrenadorApplicationService entrenadorApplicationService;

    public EntrenadorController(EntrenadorApplicationService entrenadorApplicationService) {
        this.entrenadorApplicationService = entrenadorApplicationService;
    }

    @Operation(summary = "Registrar un entrenador")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Entrenador registrado correctamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = EntrenadorDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos (nombre o especialidad vacíos)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EntrenadorDTO agregarEntrenador(@Valid @RequestBody EntrenadorDTO entrenador) {
        return entrenadorApplicationService.agregarEntrenador(entrenador);
    }

    @Operation(summary = "Listar todos los entrenadores")
    @ApiResponse(responseCode = "200", description = "Lista de entrenadores (puede estar vacía)",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = EntrenadorDTO.class))))
    @GetMapping
    public List<EntrenadorDTO> obtenerTodosEntrenadores() {
        return entrenadorApplicationService.obtenerTodosEntrenadores();
    }

    @Operation(summary = "Obtener un entrenador por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Entrenador encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = EntrenadorDTO.class))),
            @ApiResponse(responseCode = "404", description = "No existe un entrenador con ese id",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public EntrenadorDTO obtenerEntrenadorPorId(
            @Parameter(description = "Id del entrenador", example = "1") @PathVariable Long id) {
        return entrenadorApplicationService.obtenerEntrenadorPorId(id);
    }

    @Operation(summary = "Verificar si existe un entrenador",
            description = "Devuelve true/false. Lo consume class-service (vía REST) antes de programar una clase.")
    @ApiResponse(responseCode = "200", description = "true si el entrenador existe, false en caso contrario",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Boolean.class)))
    @GetMapping("/{id}/exists")
    public boolean existeEntrenador(
            @Parameter(description = "Id del entrenador a verificar", example = "1") @PathVariable Long id) {
        return entrenadorApplicationService.existeEntrenador(id);
    }
}
