package itacademy.pawalert.domain.user.exception;

public class CannotModifyLastAdminException extends RuntimeException {
    public CannotModifyLastAdminException(String message) {
        super(message);
    }
}
