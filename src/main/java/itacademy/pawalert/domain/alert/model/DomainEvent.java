package itacademy.pawalert.domain.alert.model;


import java.util.UUID;

public interface DomainEvent {
    EventType getEventType();

    UUID getAlertId();

    UserId getUserId();

    ChangedAt getChangedAt();
}