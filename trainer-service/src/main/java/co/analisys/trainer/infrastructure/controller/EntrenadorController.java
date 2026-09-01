package co.analisys.trainer.infrastructure.controller;

import co.analisys.trainer.application.dto.EntrenadorDTO;
import co.analisys.trainer.application.service.EntrenadorApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * API REST del microservicio trainer-service.
 * Expone también GET /api/trainers/{id}/exists, usado por class-service
 * para verificar la existencia de un entrenador sin acoplarse a su base de datos.
 */
@RestController
@RequestMapping("/api/trainers")
public class EntrenadorController {

    private final EntrenadorApplicationService entrenadorApplicationService;

    public EntrenadorController(EntrenadorApplicationService entrenadorApplicationService) {
        this.entrenadorApplicationService = entrenadorApplicationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EntrenadorDTO agregarEntrenador(@Valid @RequestBody EntrenadorDTO entrenador) {
        return entrenadorApplicationService.agregarEntrenador(entrenador);
    }

    @GetMapping
    public List<EntrenadorDTO> obtenerTodosEntrenadores() {
        return entrenadorApplicationService.obtenerTodosEntrenadores();
    }

    @GetMapping("/{id}")
    public EntrenadorDTO obtenerEntrenadorPorId(@PathVariable Long id) {
        return entrenadorApplicationService.obtenerEntrenadorPorId(id);
    }

    @GetMapping("/{id}/exists")
    public boolean existeEntrenador(@PathVariable Long id) {
        return entrenadorApplicationService.existeEntrenador(id);
    }
}
