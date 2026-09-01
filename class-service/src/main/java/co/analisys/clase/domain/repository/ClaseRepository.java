package co.analisys.clase.domain.repository;

import co.analisys.clase.domain.model.Clase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClaseRepository extends JpaRepository<Clase, Long> {
}
