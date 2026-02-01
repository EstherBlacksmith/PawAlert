package itacademy.pawalert.domain.exception;

public class AlertModificationNotAllowedException extends RuntimeException {

    public AlertModificationNotAllowedException(String message) {
        super(message);
    }

    public static AlertModificationNotAllowedException cannotModifyTitle(String alertId) {
        return new AlertModificationNotAllowedException(
                "Cannot modify title of alert in non-opened status: " + alertId
        );
    }

    public static AlertModificationNotAllowedException cannotModifyDescription(String alertId) {
        return new AlertModificationNotAllowedException(
                "Cannot modify description of alert in non-opened status: " + alertId
        );
    }
}
