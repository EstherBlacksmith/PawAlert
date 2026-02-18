package itacademy.pawalert.application.alert.service;

import itacademy.pawalert.application.alert.port.outbound.AlertRepositoryPort;
import itacademy.pawalert.application.exception.AlertNotFoundException;
import itacademy.pawalert.application.exception.CannotSubscribeToClosedAlertException;
import itacademy.pawalert.application.exception.SubscriptionAlreadyExistsException;
import itacademy.pawalert.application.exception.SubscriptionNotFoundException;
import itacademy.pawalert.application.alert.port.inbound.AlertSubscriptionUseCase;
import itacademy.pawalert.application.alert.port.outbound.AlertSubscriptionRepositoryPort;
import itacademy.pawalert.domain.alert.model.Alert;
import itacademy.pawalert.domain.alert.model.AlertSubscription;
import itacademy.pawalert.domain.alert.model.NotificationChannel;
import itacademy.pawalert.domain.alert.model.StatusNames;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AlertSubscriptionService implements AlertSubscriptionUseCase {

    private final AlertSubscriptionRepositoryPort subscriptionRepository;
    private final AlertRepositoryPort alertRepository;


    public AlertSubscriptionService(AlertSubscriptionRepositoryPort subscriptionRepository,
                                    AlertRepositoryPort alertRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.alertRepository = alertRepository;
    }


    @Override
    public AlertSubscription subscribeToAlert(UUID alertId, UUID userId) {
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new AlertNotFoundException(alertId.toString()));

        if (alert.currentStatus().getStatusName() == StatusNames.CLOSED) {
            throw new CannotSubscribeToClosedAlertException(alertId);
        }

        if (subscriptionRepository.existsByAlertIdAndUserId(alertId, userId)) {
            throw new SubscriptionAlreadyExistsException(
                    "User " + userId + " is already subscribed to alert " + alertId);
        }


        AlertSubscription subscription = AlertSubscription.create(alertId, userId);
        return subscriptionRepository.save(subscription);
    }

    @Override
    public void unsubscribeFromAlert(UUID alertId, UUID userId) {
        List<AlertSubscription> subscriptions = subscriptionRepository.findByUserId(userId);

        AlertSubscription subscription = subscriptions.stream()
                .filter(s -> s.getAlertId().equals(alertId))
                .findFirst()
                .orElseThrow(() -> new SubscriptionNotFoundException(
                        "Subscription not found for alert " + alertId + " and user " + userId));

        subscription.cancel();
        subscriptionRepository.save(subscription);
    }


    @Override
    public List<AlertSubscription> getUserSubscriptions(UUID userId) {
        return subscriptionRepository.findByUserId(userId);
    }

    @Override
    public List<AlertSubscription> getUserActiveSubscriptions(UUID userId) {
        return subscriptionRepository.findByUserIdAndActiveTrue(userId);
    }

    @Override
    public boolean isUserSubscribed(UUID alertId, UUID userId) {
        return subscriptionRepository.existsByAlertIdAndUserId(alertId, userId);
    }


    @Override
    public List<AlertSubscription> getActiveSubscriptionsByAlertId(UUID alertId) {
        return subscriptionRepository.findByAlertIdAndActiveTrue(alertId);
    }
}
