package co.analisys.analytics.domain.repository;

import co.analisys.analytics.domain.model.EntrenamientoRegistrado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EntrenamientoRegistradoRepository extends JpaRepository<EntrenamientoRegistrado, Long> {
    boolean existsByTopicAndParticionAndOffsetKafka(String topic, int particion, long offsetKafka);

    List<EntrenamientoRegistrado> findTop50ByOrderByIdDesc();
}
