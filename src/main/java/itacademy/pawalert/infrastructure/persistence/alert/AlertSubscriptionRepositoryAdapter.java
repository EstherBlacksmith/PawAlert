package itacademy.pawalert.infrastructure.persistence.alert;

import itacademy.pawalert.application.alert.port.outbound.AlertSubscriptionRepositoryPort;
import itacademy.pawalert.domain.alert.model.AlertSubscription;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class AlertSubscriptionRepositoryAdapter implements AlertSubscriptionRepositoryPort {


    private final AlertSubscriptionRepository jpaRepository;
    private final AlertRepository alertRepository;

    public AlertSubscriptionRepositoryAdapter(AlertSubscriptionRepository jpaRepository, AlertRepository alertRepository) {
        this.jpaRepository = jpaRepository;
        this.alertRepository = alertRepository;
    }

    @Override
    public AlertSubscription save(AlertSubscription subscription) {
        AlertEntity alertEntity = alertRepository.findById(subscription.getAlertId().toString())
                .orElseThrow(() -> new IllegalArgumentException("Alert not found with id: " + subscription.getAlertId()));
        AlertSubscriptionEntity alertSubscriptionEntity = AlertSubscriptionEntity.fromDomain(subscription, alertEntity);
        AlertSubscriptionEntity saved = jpaRepository.save(alertSubscriptionEntity);
        return saved.toDomain();
    }

    @Override
    public Optional<AlertSubscription> findById(UUID id) {
        return jpaRepository.findById(id).map(AlertSubscriptionEntity::toDomain);
    }


    @Override
    public List<AlertSubscription> findByUserId(UUID userId) {
        return jpaRepository.findByUserId(userId).stream()
                .map(AlertSubscriptionEntity::toDomain)
                .toList();
    }

    @Override
    public boolean existsByAlertIdAndUserId(UUID alertId, UUID userId) {
        return jpaRepository.existsByAlert_IdAndUserId(alertId,userId);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public void deleteAllByAlertId(UUID alertId) {
        jpaRepository.deleteAllByAlert_Id(alertId);
    }

}