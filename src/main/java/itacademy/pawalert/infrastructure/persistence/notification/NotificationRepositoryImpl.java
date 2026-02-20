package itacademy.pawalert.infrastructure.persistence.notification;

import itacademy.pawalert.application.notification.port.outbound.NotificationRepositoryPort;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class NotificationRepositoryImpl implements NotificationRepositoryPort {

    private final EntityManager entityManager;

    public NotificationRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<String> findSubscriberEmailsByAlertId(UUID alertId) {
        return entityManager.createQuery(
                        "SELECT DISTINCT u.email FROM UserEntity u " +
                                "JOIN AlertSubscriptionEntity s ON CAST(s.userId AS string) = u.id " +
                                "WHERE s.alert.id = :alertId " +
                                "AND u.emailNotificationsEnabled = true",
                        String.class)
                .setParameter("alertId", alertId.toString())
                .getResultList();
    }

    @Override
    public List<String> findSubscriberTelegramChatIdsByAlertId(UUID alertId) {
        return entityManager.createQuery(
                        "SELECT DISTINCT u.telegramChatId FROM UserEntity u " +
                                "JOIN AlertSubscriptionEntity s ON CAST(s.userId AS string) = u.id " +
                                "WHERE s.alert.id = :alertId " +
                                "AND u.telegramNotificationsEnabled = true " +
                                "AND u.telegramChatId IS NOT NULL", String.class)
                .setParameter("alertId", alertId.toString())
                .getResultList();
    }

    @Override
    public List<UUID> findSubscriberUserIdsByAlertId(UUID alertId) {
        return entityManager.createQuery(
                        "SELECT DISTINCT u.id FROM UserEntity u " +
                                "JOIN AlertSubscriptionEntity s ON CAST(s.userId AS string) = u.id " +
                                "WHERE s.alert.id = :alertId " +
                                "AND (u.emailNotificationsEnabled = true OR u.telegramNotificationsEnabled = true)",
                        UUID.class)
                .setParameter("alertId", alertId.toString())
                .getResultList();
    }

}
