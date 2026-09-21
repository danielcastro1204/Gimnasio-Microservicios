package co.analisys.member.infrastructure.controller;

import co.analisys.member.application.dto.ApiErrorResponse;
import co.analisys.member.application.dto.MiembroDTO;
import co.analisys.member.application.service.MiembroApplicationService;
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
 * API REST del microservicio member-service.
 * Conserva el endpoint funcional original del monolito (POST/GET miembros),
 * ahora bajo el recurso propio /api/members.
 */
@Tag(name = "Miembros", description = "Alta y consulta de miembros del gimnasio (bounded context Membership)")
@RestController
@RequestMapping("/api/members")
public class MiembroController {

    private final MiembroApplicationService miembroApplicationService;

    public MiembroController(MiembroApplicationService miembroApplicationService) {
        this.miembroApplicationService = miembroApplicationService;
    }

    @Operation(summary = "Registrar un miembro",
            description = "Crea un miembro nuevo. El email debe ser único en el sistema.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Miembro registrado correctamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MiembroDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos (nombre vacío o email con formato incorrecto)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Ya existe un miembro registrado con ese email",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MiembroDTO registrarMiembro(@Valid @RequestBody MiembroDTO miembro) {
        return miembroApplicationService.registrarMiembro(miembro);
    }

    @Operation(summary = "Listar todos los miembros")
    @ApiResponse(responseCode = "200", description = "Lista de miembros (puede estar vacía)",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = MiembroDTO.class))))
    @GetMapping
    public List<MiembroDTO> obtenerTodosMiembros() {
        return miembroApplicationService.obtenerTodosMiembros();
    }

    @Operation(summary = "Obtener un miembro por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Miembro encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MiembroDTO.class))),
            @ApiResponse(responseCode = "404", description = "No existe un miembro con ese id",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public MiembroDTO obtenerMiembroPorId(
            @Parameter(description = "Id del miembro", example = "1") @PathVariable Long id) {
        return miembroApplicationService.obtenerMiembroPorId(id);
    }
}
