package co.analisys.equipment.domain.repository;

import co.analisys.equipment.domain.model.Equipo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EquipoRepository extends JpaRepository<Equipo, Long> {
}
