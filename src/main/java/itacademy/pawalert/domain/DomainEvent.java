package itacademy.pawalert.domain;


import java.util.UUID;

public interface DomainEvent {
    EventType getEventType();
    UUID getAlertId();
    UserId getUserId();
    ChangedAt getChangedAt();
}