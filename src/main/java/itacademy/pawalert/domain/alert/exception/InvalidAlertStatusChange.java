package itacademy.pawalert.domain.alert.exception;

public class InvalidAlertStatusChange extends RuntimeException {
    public InvalidAlertStatusChange(String message) {
        super(message);
    }

    public static InvalidAlertStatusChange alertNotFound(String alertId) {
        return new InvalidAlertStatusChange("Alert not found: " + alertId);
    }

    public static InvalidAlertStatusChange alreadyClosed(String alertId) {
        return new InvalidAlertStatusChange("Alert is already closed: " + alertId);
    }

    public static InvalidAlertStatusChange invalidTransition(String current, String next) {
        return new InvalidAlertStatusChange(
                "Cannot transition from " + current + " to " + next
        );
    }

    public static InvalidAlertStatusChange already(String alertId) {
        return new InvalidAlertStatusChange("Alert is already closed: " + alertId);
    }

}
