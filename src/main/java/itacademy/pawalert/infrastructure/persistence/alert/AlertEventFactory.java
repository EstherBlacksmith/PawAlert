package itacademy.pawalert.infrastructure.persistence.alert;

import itacademy.pawalert.domain.alert.model.*;

import java.util.UUID;

public class AlertEventFactory {

    // Factory method for status changes
    public static AlertEvent createStatusChangedEvent(
            Alert alert, StatusNames previousStatus, StatusNames newStatus, UUID userId,GeographicLocation location ){

        return AlertEvent.createStatusEvent(previousStatus, newStatus, userId,location);
    }

    // Factory method for title changes
    public static AlertEvent createTitleChangedEvent(
            Alert alert, Title oldTitle, Title newTitle, UUID userId,GeographicLocation location) {

        return AlertEvent.createTitleEvent(oldTitle, newTitle, userId,location);
    }

    // Factory method for description changes
    public static AlertEvent createDescriptionChangedEvent(
            Alert alert, Description oldDescription, Description newDescription, UUID userId,GeographicLocation location) {

        return AlertEvent.createDescriptionEvent(oldDescription, newDescription, userId,location);
    }
}
