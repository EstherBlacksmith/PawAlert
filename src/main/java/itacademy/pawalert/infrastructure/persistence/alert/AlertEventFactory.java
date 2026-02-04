package itacademy.pawalert.infrastructure.persistence.alert;

import itacademy.pawalert.domain.alert.model.*;

public class AlertEventFactory {

    // Factory method for status changes
    public static AlertEvent createStatusChangedEvent(
            Alert alert, StatusNames previousStatus, StatusNames newStatus, UserId userId) {
        return AlertEvent.createStatusEvent(previousStatus, newStatus, userId);
    }

    // Factory method for title changes
    public static AlertEvent createTitleChangedEvent(
            Alert alert, String oldTitle, String newTitle, UserId userId) {
        return AlertEvent.createTitleEvent(oldTitle, newTitle, userId);
    }

    // Factory method for description changes
    public static AlertEvent createDescriptionChangedEvent(
            Alert alert, String oldDescription, String newDescription, UserId userId) {
        return AlertEvent.createDescriptionEvent(oldDescription, newDescription, userId);
    }
}
