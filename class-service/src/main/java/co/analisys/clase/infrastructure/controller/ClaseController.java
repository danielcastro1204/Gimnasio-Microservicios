package co.analisys.clase.infrastructure.controller;

import co.analisys.clase.application.dto.ClaseDTO;
import co.analisys.clase.application.service.ClaseApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * API REST del microservicio class-service.
 * Al programar una clase, este microservicio valida vía REST contra
 * trainer-service que el entrenador exista (GET /api/trainers/{id}/exists).
 */
@RestController
@RequestMapping("/api/classes")
public class ClaseController {

    private final ClaseApplicationService claseApplicationService;

    public ClaseController(ClaseApplicationService claseApplicationService) {
        this.claseApplicationService = claseApplicationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClaseDTO programarClase(@Valid @RequestBody ClaseDTO clase) {
        return claseApplicationService.programarClase(clase);
    }

    @GetMapping
    public List<ClaseDTO> obtenerTodasClases() {
        return claseApplicationService.obtenerTodasClases();
    }

    @GetMapping("/{id}")
    public ClaseDTO obtenerClasePorId(@PathVariable Long id) {
        return claseApplicationService.obtenerClasePorId(id);
    }
}
