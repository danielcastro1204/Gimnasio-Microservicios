package co.analisys.member.domain.repository;

import co.analisys.member.domain.model.Miembro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MiembroRepository extends JpaRepository<Miembro, Long> {
    Optional<Miembro> findByEmail(String email);
}
