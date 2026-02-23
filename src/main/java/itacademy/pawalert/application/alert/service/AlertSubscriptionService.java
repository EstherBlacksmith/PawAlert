package itacademy.pawalert.application.alert.service;

import itacademy.pawalert.application.alert.port.inbound.AlertSubscriptionUseCase;
import itacademy.pawalert.application.alert.port.outbound.AlertRepositoryPort;
import itacademy.pawalert.application.alert.port.outbound.AlertSubscriptionRepositoryPort;
import itacademy.pawalert.application.exception.AlertNotFoundException;
import itacademy.pawalert.application.exception.CannotSubscribeToClosedAlertException;
import itacademy.pawalert.application.exception.SubscriptionAlreadyExistsException;
import itacademy.pawalert.application.exception.SubscriptionNotFoundException;
import itacademy.pawalert.domain.alert.model.Alert;
import itacademy.pawalert.domain.alert.model.AlertSubscription;
import itacademy.pawalert.domain.alert.model.StatusNames;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class AlertSubscriptionService implements AlertSubscriptionUseCase {

    private final AlertSubscriptionRepositoryPort subscriptionRepository;
    private final AlertRepositoryPort alertRepository;


    public AlertSubscriptionService(AlertSubscriptionRepositoryPort subscriptionRepository,
                                    AlertRepositoryPort alertRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.alertRepository = alertRepository;
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public AlertSubscription subscribeToAlert(UUID alertId, UUID userId) {
        log.info("[SUBSCRIBE-SERVICE] Subscribing user {} to alert {}", userId, alertId);

        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> {
                    log.error("[SUBSCRIBE-SERVICE] Alert not found: {}", alertId);
                    return new AlertNotFoundException(alertId.toString());
                });

        log.info("[SUBSCRIBE-SERVICE] Found alert with status: {}", alert.currentStatus().getStatusName());

        if (alert.currentStatus().getStatusName() == StatusNames.CLOSED) {
            log.error("[SUBSCRIBE-SERVICE] Cannot subscribe to closed alert: {}", alertId);
            throw new CannotSubscribeToClosedAlertException(alertId);
        }

        if (subscriptionRepository.existsByAlertIdAndUserId(alertId, userId)) {
            log.error("[SUBSCRIBE-SERVICE] User {} already subscribed to alert {}", userId, alertId);
            throw new SubscriptionAlreadyExistsException(
                    "User " + userId + " is already subscribed to alert " + alertId);
        }

        log.info("[SUBSCRIBE-SERVICE] Creating new subscription");
        AlertSubscription subscription = AlertSubscription.create(alertId, userId);
        AlertSubscription saved = subscriptionRepository.save(subscription);
        log.info("[SUBSCRIBE-SERVICE] Subscription saved with ID: {}", saved.id());
        return saved;
    }

    @Override
    public void unsubscribeFromAlert(UUID alertId, UUID userId) {
        List<AlertSubscription> subscriptions = subscriptionRepository.findByUserId(userId);

        AlertSubscription subscription = subscriptions.stream()
                .filter(s -> s.alertId().equals(alertId))
                .findFirst()
                .orElseThrow(() -> new SubscriptionNotFoundException(
                        "Subscription not found for alert " + alertId + " and user " + userId));

        subscriptionRepository.deleteById(subscription.id());
    }


    @Override
    public List<AlertSubscription> getUserSubscriptions(UUID userId) {
        return subscriptionRepository.findByUserId(userId);
    }

    @Override
    public boolean isUserSubscribed(UUID alertId, UUID userId) {
        return subscriptionRepository.existsByAlertIdAndUserId(alertId, userId);
    }

}
