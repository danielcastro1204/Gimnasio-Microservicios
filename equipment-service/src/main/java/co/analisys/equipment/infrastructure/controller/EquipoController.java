package co.analisys.equipment.infrastructure.controller;

import co.analisys.equipment.application.dto.EquipoDTO;
import co.analisys.equipment.application.service.EquipoApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/equipment")
public class EquipoController {

    private final EquipoApplicationService equipoApplicationService;

    public EquipoController(EquipoApplicationService equipoApplicationService) {
        this.equipoApplicationService = equipoApplicationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EquipoDTO agregarEquipo(@Valid @RequestBody EquipoDTO equipo) {
        return equipoApplicationService.agregarEquipo(equipo);
    }

    @GetMapping
    public List<EquipoDTO> obtenerTodosEquipos() {
        return equipoApplicationService.obtenerTodosEquipos();
    }

    @GetMapping("/{id}")
    public EquipoDTO obtenerEquipoPorId(@PathVariable Long id) {
        return equipoApplicationService.obtenerEquipoPorId(id);
    }
}
