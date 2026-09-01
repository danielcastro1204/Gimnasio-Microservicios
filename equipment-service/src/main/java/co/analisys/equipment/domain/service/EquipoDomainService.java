package co.analisys.equipment.domain.service;

import co.analisys.equipment.domain.model.Equipo;
import co.analisys.equipment.domain.repository.EquipoRepository;
import co.analisys.equipment.infrastructure.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class EquipoDomainService {

    private final EquipoRepository equipoRepository;

    public EquipoDomainService(EquipoRepository equipoRepository) {
        this.equipoRepository = equipoRepository;
    }

    public Equipo obtenerPorIdOFallar(Long id) {
        return equipoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un equipo con id: " + id));
    }
}
