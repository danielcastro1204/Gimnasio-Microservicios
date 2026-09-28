package co.analisys.analytics.domain.repository;

import co.analisys.analytics.domain.model.KafkaCheckpoint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface KafkaCheckpointRepository extends JpaRepository<KafkaCheckpoint, Long> {
    List<KafkaCheckpoint> findByTopicOrderByParticion(String topic);

    Optional<KafkaCheckpoint> findByTopicAndParticion(String topic, int particion);
}
