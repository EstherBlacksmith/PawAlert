package itacademy.pawalert.application.alert.port.inbound;

import itacademy.pawalert.domain.alert.model.AlertSubscription;

import java.util.List;
import java.util.UUID;

public interface AlertSubscriptionUseCase {
    AlertSubscription subscribeToAlert(UUID alertId, UUID userId);

    void unsubscribeFromAlert(UUID alertId, UUID userId);

    List<AlertSubscription> getUserSubscriptions(UUID userId);

    boolean isUserSubscribed(UUID alertId, UUID userId);
}
