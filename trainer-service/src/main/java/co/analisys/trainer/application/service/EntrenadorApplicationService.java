package co.analisys.trainer.application.service;

import co.analisys.trainer.application.dto.EntrenadorDTO;
import co.analisys.trainer.domain.model.Entrenador;
import co.analisys.trainer.domain.repository.EntrenadorRepository;
import co.analisys.trainer.domain.service.EntrenadorDomainService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EntrenadorApplicationService {

    private final EntrenadorRepository entrenadorRepository;
    private final EntrenadorDomainService entrenadorDomainService;

    public EntrenadorApplicationService(EntrenadorRepository entrenadorRepository,
                                         EntrenadorDomainService entrenadorDomainService) {
        this.entrenadorRepository = entrenadorRepository;
        this.entrenadorDomainService = entrenadorDomainService;
    }

    @Transactional
    public EntrenadorDTO agregarEntrenador(EntrenadorDTO dto) {
        Entrenador entrenador = toEntity(dto);
        Entrenador guardado = entrenadorRepository.save(entrenador);
        return toDTO(guardado);
    }

    public List<EntrenadorDTO> obtenerTodosEntrenadores() {
        return entrenadorRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public EntrenadorDTO obtenerEntrenadorPorId(Long id) {
        return toDTO(entrenadorDomainService.obtenerPorIdOFallar(id));
    }

    public boolean existeEntrenador(Long id) {
        return entrenadorRepository.existsById(id);
    }

    private Entrenador toEntity(EntrenadorDTO dto) {
        Entrenador e = new Entrenador();
        e.setId(dto.getId());
        e.setNombre(dto.getNombre());
        e.setEspecialidad(dto.getEspecialidad());
        return e;
    }

    private EntrenadorDTO toDTO(Entrenador e) {
        return new EntrenadorDTO(e.getId(), e.getNombre(), e.getEspecialidad());
    }
}
