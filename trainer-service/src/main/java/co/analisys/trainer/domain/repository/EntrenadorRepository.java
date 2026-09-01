package co.analisys.trainer.domain.repository;

import co.analisys.trainer.domain.model.Entrenador;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EntrenadorRepository extends JpaRepository<Entrenador, Long> {
}
