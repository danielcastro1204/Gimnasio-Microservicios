package co.analisys.notification.domain.repository;

import co.analisys.notification.domain.model.OcupacionClase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OcupacionClaseRepository extends JpaRepository<OcupacionClase, Long> {
    Optional<OcupacionClase> findByClaseId(Long claseId);
}
