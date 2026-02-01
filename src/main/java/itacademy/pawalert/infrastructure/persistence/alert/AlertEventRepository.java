package itacademy.pawalert.infrastructure.persistence.alert;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlertEventRepository extends JpaRepository<AlertEventEntity, String> {

    /**
     * Looks for all the events sort by date descendant
     */
    List<AlertEventEntity> findByAlertIdOrderByChangedAtDesc(String alertId);

    /**
     * Looks for all the event in one alert.
     */
    List<AlertEventEntity> findByAlertId(String alertId);
}