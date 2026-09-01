package co.analisys.member.application.service;

import co.analisys.member.application.dto.MiembroDTO;
import co.analisys.member.domain.model.Miembro;
import co.analisys.member.domain.repository.MiembroRepository;
import co.analisys.member.domain.service.MiembroDomainService;
import co.analisys.member.infrastructure.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de aplicación: orquesta casos de uso del bounded context "Membership"
 * y traduce entre el modelo de dominio y los DTOs expuestos por la API.
 */
@Service
public class MiembroApplicationService {

    private final MiembroRepository miembroRepository;
    private final MiembroDomainService miembroDomainService;

    public MiembroApplicationService(MiembroRepository miembroRepository,
                                      MiembroDomainService miembroDomainService) {
        this.miembroRepository = miembroRepository;
        this.miembroDomainService = miembroDomainService;
    }

    @Transactional
    public MiembroDTO registrarMiembro(MiembroDTO dto) {
        miembroDomainService.validarEmailUnico(dto.getEmail());
        Miembro miembro = toEntity(dto);
        Miembro guardado = miembroRepository.save(miembro);
        return toDTO(guardado);
    }

    public List<MiembroDTO> obtenerTodosMiembros() {
        return miembroRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public MiembroDTO obtenerMiembroPorId(Long id) {
        Miembro miembro = miembroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un miembro con id: " + id));
        return toDTO(miembro);
    }

    private Miembro toEntity(MiembroDTO dto) {
        Miembro miembro = new Miembro();
        miembro.setId(dto.getId());
        miembro.setNombre(dto.getNombre());
        miembro.setEmail(dto.getEmail());
        miembro.setFechaInscripcion(dto.getFechaInscripcion());
        return miembro;
    }

    private MiembroDTO toDTO(Miembro miembro) {
        return new MiembroDTO(miembro.getId(), miembro.getNombre(), miembro.getEmail(), miembro.getFechaInscripcion());
    }
}
