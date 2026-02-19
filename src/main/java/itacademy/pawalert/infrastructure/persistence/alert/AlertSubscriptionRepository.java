package itacademy.pawalert.infrastructure.persistence.alert;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface AlertSubscriptionRepository  extends JpaRepository<AlertSubscriptionEntity, UUID> {

    List<AlertSubscriptionEntity> findByUserId(UUID userId);
    boolean existsByAlertIdAndUserId(UUID alertId, UUID userId);
    void deleteAllByAlertId(UUID alertId);
    void deleteByAlertIdAndUserId(UUID alertId, UUID userID);
}
