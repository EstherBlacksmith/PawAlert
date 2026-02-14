package itacademy.pawalert.domain.alert.model;


import java.util.UUID;

public interface DomainEvent {
    EventType getEventType();

    UUID getAlertId();

    UUID getUserId();

    ChangedAt getChangedAt();
}