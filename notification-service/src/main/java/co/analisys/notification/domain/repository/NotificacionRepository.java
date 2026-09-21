package co.analisys.notification.domain.repository;

import co.analisys.notification.domain.model.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
}
