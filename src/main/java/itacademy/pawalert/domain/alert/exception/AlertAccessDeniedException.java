package itacademy.pawalert.domain.alert.exception;

import java.util.UUID;


public class AlertAccessDeniedException extends RuntimeException {

    public AlertAccessDeniedException(UUID alertId, UUID userId) {
        super(String.format("User %s is not authorized to modify alert %s. Only the alert owner or an administrator can perform this action.",
                userId, alertId));
    }

    public AlertAccessDeniedException(String message) {
        super(message);
    }
}
