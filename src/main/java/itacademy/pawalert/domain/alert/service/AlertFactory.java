package itacademy.pawalert.domain.alert.service;

import itacademy.pawalert.domain.alert.model.*;

import java.util.UUID;

public class AlertFactory {

    public static Alert createAlert(UUID petId, UUID userId, Title title, Description description) {
        return new Alert(UUID.randomUUID(), petId, userId, title, description, new OpenedStateAlert());
    }

    public static Alert markAsSeen(Alert alert) {
        return new Alert(alert.getId(), alert.getPetId(), alert.getUserId(),
                alert.getTitle(), alert.getDescription(), new SeenStatusAlert());
    }

    public static Alert markAsSafe(Alert alert) {
        return new Alert(alert.getId(), alert.getPetId(), alert.getUserId(),
                alert.getTitle(), alert.getDescription(), new SafeStatusAlert());
    }

    public static Alert markAsClose(Alert alert) {
        return new Alert(alert.getId(), alert.getPetId(), alert.getUserId(),
                alert.getTitle(), alert.getDescription(), new ClosedStatusAlert());
    }
}
