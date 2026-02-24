package itacademy.pawalert.domain.notification.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

public interface NotificationEvent extends Serializable {

    UUID eventId();
    UUID userId();
    UUID alertId();
    LocalDateTime createdAt();
    int retryCount();

    NotificationEvent withIncrementedRetry();
}