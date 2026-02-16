package itacademy.pawalert.infrastructure.persistence.alert;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AlertEventRepository extends JpaRepository<AlertEventEntity, String> {

    List<AlertEventEntity> findByAlert_IdOrderByChangedAtDesc(String alertId);

    List<AlertEventEntity> findByAlert_Id(String alertId);

    Optional<AlertEventEntity> findFirstByAlert_IdOrderByChangedAtDesc(String alertId);


    @Query("SELECT e FROM AlertEventEntity e JOIN FETCH e.alert WHERE e.alert.id = :alertId ORDER BY e.changedAt DESC")
    List<AlertEventEntity> findByAlertIdWithAlertOrderByChangedAtDesc(String alertId);

    @Query("SELECT e FROM AlertEventEntity e JOIN FETCH e.alert WHERE e.alert.id = :alertId ORDER BY e.changedAt DESC LIMIT 1")
    Optional<AlertEventEntity> findFirstByAlertIdWithAlertOrderByChangedAtDesc(String alertId);
}