package co.analisys.member.domain.repository;

import co.analisys.member.domain.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PagoRepository extends JpaRepository<Pago, Long> {
}
