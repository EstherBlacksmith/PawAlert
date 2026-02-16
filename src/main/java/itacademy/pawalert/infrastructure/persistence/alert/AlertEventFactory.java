package itacademy.pawalert.infrastructure.persistence.alert;

import itacademy.pawalert.domain.alert.model.*;

import java.util.UUID;

public class AlertEventFactory {

    // Factory method for status changes
    public static AlertEvent createStatusChangedEvent(
            Alert alert, StatusNames previousStatus, StatusNames newStatus, UUID userId, GeographicLocation location ){

        return AlertEvent.createStatusEvent(alert.getId(), previousStatus, newStatus, userId, location);
    }

    // Factory method for closure events (includes closure reason)
    public static AlertEvent createClosureEvent(
            Alert alert, StatusNames previousStatus, UUID userId, GeographicLocation location, ClosureReason closureReason ){

        return AlertEvent.createClosureEvent(alert.getId(), previousStatus, userId, location, closureReason);
    }

    // Factory method for title changes
    public static AlertEvent createTitleChangedEvent(
            Alert alert, Title oldTitle, Title newTitle, UUID userId) {

        return AlertEvent.createTitleEvent(alert.getId(), oldTitle, newTitle, userId);
    }

    // Factory method for description changes
    public static AlertEvent createDescriptionChangedEvent(
            Alert alert, Description oldDescription, Description newDescription, UUID userId) {

        return AlertEvent.createDescriptionEvent(alert.getId(), oldDescription, newDescription, userId);
    }
}
