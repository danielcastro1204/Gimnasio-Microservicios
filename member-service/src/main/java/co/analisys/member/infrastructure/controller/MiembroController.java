package co.analisys.member.infrastructure.controller;

import co.analisys.member.application.dto.MiembroDTO;
import co.analisys.member.application.service.MiembroApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * API REST del microservicio member-service.
 * Conserva el endpoint funcional original del monolito (POST/GET miembros),
 * ahora bajo el recurso propio /api/members.
 */
@RestController
@RequestMapping("/api/members")
public class MiembroController {

    private final MiembroApplicationService miembroApplicationService;

    public MiembroController(MiembroApplicationService miembroApplicationService) {
        this.miembroApplicationService = miembroApplicationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MiembroDTO registrarMiembro(@Valid @RequestBody MiembroDTO miembro) {
        return miembroApplicationService.registrarMiembro(miembro);
    }

    @GetMapping
    public List<MiembroDTO> obtenerTodosMiembros() {
        return miembroApplicationService.obtenerTodosMiembros();
    }

    @GetMapping("/{id}")
    public MiembroDTO obtenerMiembroPorId(@PathVariable Long id) {
        return miembroApplicationService.obtenerMiembroPorId(id);
    }
}
