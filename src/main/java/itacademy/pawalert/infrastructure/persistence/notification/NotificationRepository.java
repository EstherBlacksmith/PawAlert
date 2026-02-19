package itacademy.pawalert.infrastructure.persistence.notification;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository {
    List<String> findSubscriberEmailsByAlertId(UUID alertId);
    List<String> findSubscriberTelegramChatIdsByAlertId(UUID alertId);

}
