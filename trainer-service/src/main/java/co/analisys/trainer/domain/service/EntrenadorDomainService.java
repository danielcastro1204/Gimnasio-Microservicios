package co.analisys.trainer.domain.service;

import co.analisys.trainer.domain.model.Entrenador;
import co.analisys.trainer.domain.repository.EntrenadorRepository;
import co.analisys.trainer.infrastructure.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Servicio de dominio: encapsula reglas del agregado Entrenador,
 * incluida la verificación de existencia usada por otros microservicios (ej. class-service).
 */
@Service
public class EntrenadorDomainService {

    private final EntrenadorRepository entrenadorRepository;

    public EntrenadorDomainService(EntrenadorRepository entrenadorRepository) {
        this.entrenadorRepository = entrenadorRepository;
    }

    public Entrenador obtenerPorIdOFallar(Long id) {
        return entrenadorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un entrenador con id: " + id));
    }
}
