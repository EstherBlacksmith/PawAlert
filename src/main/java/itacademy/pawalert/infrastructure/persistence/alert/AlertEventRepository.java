package itacademy.pawalert.infrastructure.persistence.alert;

import aj.org.objectweb.asm.commons.Remapper;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface AlertEventRepository extends JpaRepository<AlertEventEntity, String> {

    /**
     * Looks for all the events sort by date descendant
     */
    List<AlertEventEntity> findByAlertIdOrderByChangedAtDesc(String alertId);

    /**
     * Looks for all the event in one alert.
     */
    List<AlertEventEntity> findByAlertId(String alertId);

    Optional<AlertEventEntity> findFirstByAlertIdOrderByChangedAtDesc(String string);
}