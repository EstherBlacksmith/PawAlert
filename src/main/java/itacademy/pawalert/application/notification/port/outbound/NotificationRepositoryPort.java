package itacademy.pawalert.application.notification.port.outbound;

import java.util.List;
import java.util.UUID;

public interface NotificationRepositoryPort {
    List<String> findSubscriberEmailsByAlertId(UUID alertId);

    List<String> findSubscriberTelegramChatIdsByAlertId(UUID alertId);

    List<UUID> findSubscriberUserIdsByAlertId(UUID alertId);
}
