package itacademy.pawalert.application.exception;

import java.util.UUID;

public class CannotSubscribeToClosedAlertException extends RuntimeException {
    public CannotSubscribeToClosedAlertException(UUID alertId) {
        super("Cannot subscribe to closed alert: " + alertId);
    }
}