package itacademy.pawalert.application.service;

import itacademy.pawalert.application.exception.SubscriptionAlreadyExistsException;
import itacademy.pawalert.application.exception.SubscriptionNotFoundException;
import itacademy.pawalert.application.port.inbound.AlertSubscriptionUseCase;
import itacademy.pawalert.application.port.outbound.AlertSubscriptionRepositoryPort;
import itacademy.pawalert.domain.alert.model.Alert;
import itacademy.pawalert.domain.alert.model.AlertSubscription;
import itacademy.pawalert.domain.alert.model.NotificationChannel;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AlertSubscriptionService implements AlertSubscriptionUseCase {

    private final AlertSubscriptionRepositoryPort subscriptionRepository;

    public AlertSubscriptionService(AlertSubscriptionRepositoryPort subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    @Override
    public AlertSubscription subscribeToAlert(UUID alertId, UUID userId) {
        return subscribeToAlert(alertId,userId,NotificationChannel.ALL);
    }

    @Override
    public AlertSubscription subscribeToAlert(UUID alertId, UUID userId, NotificationChannel channel) {
        if (subscriptionRepository.existsByAlertIdAndUserId(alertId, userId)) {
            throw new SubscriptionAlreadyExistsException(
                    "User " + userId + " is already subscribed to alert " + alertId);
        }

        AlertSubscription subscription = AlertSubscription.create(alertId, userId, channel);
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
    public AlertSubscription resubscribeToAlert(UUID alertId, UUID userId) {
        List<AlertSubscription> subscriptions = subscriptionRepository.findByUserId(userId);
        AlertSubscription subscription = subscriptions.stream()
                .filter(alertSubscription -> alertSubscription.getAlertId().equals(alertId))
                .findFirst()
                .orElseThrow(() -> new SubscriptionNotFoundException(
                        "Subscription not found for alert " + alertId + " and user " + userId));
        subscription.reactivate();

        subscriptionRepository.save(subscription);

        return subscription;
    }

    @Override
    public List<AlertSubscription> getUserSubscriptions(UUID userId) {
        return subscriptionRepository.findByUserId(userId);
    }

    @Override
    public List<AlertSubscription> getUserActiveSubscriptions(UUID userId) {
        return  subscriptionRepository.findByUserIdAndActiveTrue(userId);
    }

    @Override
    public boolean isUserSubscribed(UUID alertId, UUID userId) {
        return subscriptionRepository.existsByAlertIdAndUserId(alertId,userId);
    }

    @Override
    public AlertSubscription changeNotificationChannel(UUID alertId, UUID userId, NotificationChannel newChannel) {

        List<AlertSubscription> subscriptions = subscriptionRepository.findByUserId(userId);
        AlertSubscription subscription = subscriptions.stream()
                .filter(alertSubscription -> alertSubscription.getAlertId().equals(alertId))
                .findFirst()
                .orElseThrow(() -> new SubscriptionNotFoundException(
                        "Subscription not found for alert " + alertId + " and user " + userId));
        subscription.changeChannel(newChannel);

        return  subscriptionRepository.save(subscription);
    }

    @Override
    public List<AlertSubscription> getActiveSubscriptionsByAlertId(UUID alertId) {
        return subscriptionRepository.findByAlertIdAndActiveTrue(alertId);
    }
}
