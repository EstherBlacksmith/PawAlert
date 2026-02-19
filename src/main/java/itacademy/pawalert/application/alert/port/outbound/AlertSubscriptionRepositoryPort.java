package itacademy.pawalert.application.alert.port.outbound;

import itacademy.pawalert.domain.alert.model.AlertSubscription;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AlertSubscriptionRepositoryPort {
    AlertSubscription save(AlertSubscription subscription);
    Optional<AlertSubscription> findById(UUID id);
    List<AlertSubscription> findByAlertIdAndActiveTrue(UUID alertId);
    List<AlertSubscription> findByUserId(UUID userId);
    List<AlertSubscription> findByUserIdAndActiveTrue(UUID userId);
    boolean existsByAlertIdAndUserId(UUID alertId, UUID userId);
    void deleteById(UUID id);
    void deleteAllByAlertId(UUID alertId);
    List<String>findEmailsByAlertIdAndActiveTrue(UUID  alertId);
    List<String> findTelegramChatIdsByAlertId(UUID  alertId);
}
