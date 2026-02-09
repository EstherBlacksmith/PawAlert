package itacademy.pawalert.application.alert.port.inbound;

import itacademy.pawalert.domain.alert.model.AlertSubscription;
import itacademy.pawalert.domain.alert.model.NotificationChannel;

import java.util.List;
import java.util.UUID;

public interface AlertSubscriptionUseCase {
    AlertSubscription subscribeToAlert(UUID alertId, UUID userId);
    AlertSubscription subscribeToAlert(UUID alertId, UUID userId, NotificationChannel channel);
    void unsubscribeFromAlert(UUID alertId, UUID userId);
    AlertSubscription resubscribeToAlert(UUID alertId, UUID userId);
    List<AlertSubscription> getUserSubscriptions(UUID userId);
    List<AlertSubscription> getUserActiveSubscriptions(UUID userId);
    boolean isUserSubscribed(UUID alertId, UUID userId);
    AlertSubscription changeNotificationChannel(UUID alertId, UUID userId, NotificationChannel newChannel);
    List<AlertSubscription> getActiveSubscriptionsByAlertId(UUID AlertId);
}
