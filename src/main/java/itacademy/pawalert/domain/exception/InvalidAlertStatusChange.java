package itacademy.pawalert.domain.exception;

public class InvalidAlertStatusChange extends RuntimeException {
    public InvalidAlertStatusChange(String message) {
        super(message);
    }
}
