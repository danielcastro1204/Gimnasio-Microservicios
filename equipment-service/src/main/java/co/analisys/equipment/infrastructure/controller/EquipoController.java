package co.analisys.equipment.infrastructure.controller;

import co.analisys.equipment.application.dto.ApiErrorResponse;
import co.analisys.equipment.application.dto.EquipoDTO;
import co.analisys.equipment.application.service.EquipoApplicationService;
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

@Tag(name = "Equipos", description = "Alta y consulta del inventario de equipos (bounded context Inventory)")
@RestController
@RequestMapping("/api/equipment")
public class EquipoController {

    private final EquipoApplicationService equipoApplicationService;

    public EquipoController(EquipoApplicationService equipoApplicationService) {
        this.equipoApplicationService = equipoApplicationService;
    }

    @Operation(summary = "Agregar un equipo al inventario")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Equipo agregado correctamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = EquipoDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos (nombre vacío o cantidad negativa)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EquipoDTO agregarEquipo(@Valid @RequestBody EquipoDTO equipo) {
        return equipoApplicationService.agregarEquipo(equipo);
    }

    @Operation(summary = "Listar todos los equipos")
    @ApiResponse(responseCode = "200", description = "Lista de equipos (puede estar vacía)",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = EquipoDTO.class))))
    @GetMapping
    public List<EquipoDTO> obtenerTodosEquipos() {
        return equipoApplicationService.obtenerTodosEquipos();
    }

    @Operation(summary = "Obtener un equipo por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Equipo encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = EquipoDTO.class))),
            @ApiResponse(responseCode = "404", description = "No existe un equipo con ese id",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public EquipoDTO obtenerEquipoPorId(
            @Parameter(description = "Id del equipo", example = "1") @PathVariable Long id) {
        return equipoApplicationService.obtenerEquipoPorId(id);
    }
}
