package co.analisys.equipment.application.service;

import co.analisys.equipment.application.dto.EquipoDTO;
import co.analisys.equipment.domain.model.Equipo;
import co.analisys.equipment.domain.repository.EquipoRepository;
import co.analisys.equipment.domain.service.EquipoDomainService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EquipoApplicationService {

    private final EquipoRepository equipoRepository;
    private final EquipoDomainService equipoDomainService;

    public EquipoApplicationService(EquipoRepository equipoRepository, EquipoDomainService equipoDomainService) {
        this.equipoRepository = equipoRepository;
        this.equipoDomainService = equipoDomainService;
    }

    @Transactional
    public EquipoDTO agregarEquipo(EquipoDTO dto) {
        Equipo equipo = toEntity(dto);
        Equipo guardado = equipoRepository.save(equipo);
        return toDTO(guardado);
    }

    public List<EquipoDTO> obtenerTodosEquipos() {
        return equipoRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public EquipoDTO obtenerEquipoPorId(Long id) {
        return toDTO(equipoDomainService.obtenerPorIdOFallar(id));
    }

    private Equipo toEntity(EquipoDTO dto) {
        Equipo e = new Equipo();
        e.setId(dto.getId());
        e.setNombre(dto.getNombre());
        e.setDescripcion(dto.getDescripcion());
        e.setCantidad(dto.getCantidad());
        return e;
    }

    private EquipoDTO toDTO(Equipo e) {
        return new EquipoDTO(e.getId(), e.getNombre(), e.getDescripcion(), e.getCantidad());
    }
}
