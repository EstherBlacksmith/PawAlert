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

    public AlertSubscriptionRepositoryAdapter(AlertSubscriptionRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public AlertSubscription save(AlertSubscription subscription) {
        AlertSubscriptionEntity alertSubscriptionEntity = AlertSubscriptionEntity.fromDomain(subscription);
        AlertSubscriptionEntity saved = jpaRepository.save(alertSubscriptionEntity);
        return saved.toDomain();
    }

    @Override
    public Optional<AlertSubscription> findById(UUID id) {
        return jpaRepository.findById(id).map(AlertSubscriptionEntity::toDomain);
    }

    @Override
    public List<AlertSubscription> findByAlertIdAndActiveTrue(UUID alertId) {
        return jpaRepository.findByAlertIdAndActiveTrue(alertId).stream()
                .map(AlertSubscriptionEntity::toDomain)
                .toList();
    }

    @Override
    public List<AlertSubscription> findByUserId(UUID userId) {
        return jpaRepository.findByUserId(userId).stream()
                .map(AlertSubscriptionEntity::toDomain)
                .toList();
    }

    @Override
    public List<AlertSubscription> findByUserIdAndActiveTrue(UUID userId) {
        return jpaRepository.findByUserIdAndActiveTrue(userId).stream()
                .map(AlertSubscriptionEntity::toDomain)
                .toList();
    }

    @Override
    public boolean existsByAlertIdAndUserId(UUID alertId, UUID userId) {
        return jpaRepository.existsByAlertIdAndUserId(alertId,userId);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public void deleteAllByAlertId(UUID alertId) {
        jpaRepository.deleteAllByAlertId(alertId);

    }

    @Override
    public List<String> findEmailsByAlertIdAndActiveTrue(UUID  alertId) {
        return jpaRepository.findEmailsByAlertIdAndActiveTrue(alertId);
    }

    @Override
    public List<String> findTelegramChatIdsByAlertId(UUID  alertId) {
        return jpaRepository.findTelegramChatIdsByAlertId(alertId);
    }

}