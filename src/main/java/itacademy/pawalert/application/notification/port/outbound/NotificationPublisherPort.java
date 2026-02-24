package itacademy.pawalert.application.notification.port.outbound;

import itacademy.pawalert.domain.alert.model.NotificationChannel;
import itacademy.pawalert.domain.notification.model.NotificationEvent;

public interface NotificationPublisherPort<T extends NotificationEvent> {
    void publish(T event);

    NotificationChannel getChannel();
}