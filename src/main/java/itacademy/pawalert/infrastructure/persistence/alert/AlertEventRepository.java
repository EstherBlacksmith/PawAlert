package itacademy.pawalert.infrastructure.persistence.alert;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AlertEventRepository extends JpaRepository<AlertEventEntity, UUID> {

    /**
     * Looks for all the events sort by date descendant
     */
    List<AlertEventEntity> findByAlertIdOrderByChangedAtDesc(UUID alertId);

    /**
     * Looks for all the event in one alert.
     */
    List<AlertEventEntity> findByAlertId(UUID alertId);

    Optional<AlertEventEntity> findFirstByAlertIdOrderByChangedAtDesc(UUID alertId);
}
