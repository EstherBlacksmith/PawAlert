package itacademy.pawalert.infrastructure.persistence.alert;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface AlertSubscriptionRepository  extends JpaRepository<AlertSubscriptionEntity, UUID> {

    List<AlertSubscriptionEntity> findByUserId(UUID userId);
    boolean existsByAlert_IdAndUserId(UUID alertId, UUID userId);
    void deleteAllByAlert_Id(UUID alertId);
    void deleteByAlert_IdAndUserId(UUID alertId, UUID userID);
}
