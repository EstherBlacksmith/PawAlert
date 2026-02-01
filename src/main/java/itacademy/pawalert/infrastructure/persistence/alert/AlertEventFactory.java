package itacademy.pawalert.infrastructure.persistence.alert;

import itacademy.pawalert.domain.alert.model.Alert;
import itacademy.pawalert.domain.alert.model.ChangedAt;
import itacademy.pawalert.domain.alert.model.StatusNames;
import itacademy.pawalert.domain.alert.model.UserId;

public class AlertEventFactory {

    // Factory method for status changes
    public static AlertEventEntity createStatusChangedEvent(
            Alert alert, StatusNames previousStatus, StatusNames newStatus, UserId userId) {
        return new AlertEventEntity(
                alert.getId().toString(),
                previousStatus.name(),
                newStatus.name(),
                ChangedAt.now().value(),
                userId.value()
        );
    }

    // Factory method for title changes
    public static AlertEventEntity createTitleChangedEvent(
            Alert alert, String oldTitle, String newTitle, UserId userId) {
        return new AlertEventEntity(
                alert.getId().toString(),
                "TITLE_CHANGED",  // Use this field for event type
                oldTitle,
                newTitle,
                ChangedAt.now().value(),
                userId.value()
        );
    }

    // Factory method for description changes
    public static AlertEventEntity createDescriptionChangedEvent(
            Alert alert, String oldDescription, String newDescription, UserId userId) {
        return new AlertEventEntity(
                alert.getId().toString(),
                "DESCRIPTION_CHANGED",  // Use this field for event type
                oldDescription,
                newDescription,
                ChangedAt.now().value(),
                userId.value()
        );
    }
}
