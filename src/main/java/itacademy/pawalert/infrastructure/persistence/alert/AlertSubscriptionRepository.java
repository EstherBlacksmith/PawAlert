package itacademy.pawalert.infrastructure.persistence.alert;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.UUID;

public interface AlertSubscriptionRepository  extends JpaRepository<AlertSubscriptionEntity, UUID> {
    List<AlertSubscriptionEntity> findByAlertIdAndActiveTrue(UUID alertId);
    List<AlertSubscriptionEntity> findByUserId(UUID userId);
    List<AlertSubscriptionEntity> findByUserIdAndActiveTrue(UUID userId);
    boolean existsByAlertIdAndUserId(UUID alertId, UUID userId);
    void deleteAllByAlertId(UUID alertId);

    @Query(value = "SELECT DISTINCT u.email FROM users u " +
            "INNER JOIN alert_subscriptions s ON u.id::uuid = s.user_id " +
            "WHERE s.alert_id = :alertId AND s.active = true", 
           nativeQuery = true)
    List<String> findEmailsByAlertIdAndActiveTrue(@Param("alertId") UUID alertId);
}

