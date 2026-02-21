package itacademy.pawalert.application.exception;

public class ForbiddenException extends RuntimeException {

    public ForbiddenException(String message) {
        super(message);
    }

    public static ForbiddenException notAuthorized(String action) {
        return new ForbiddenException(
                "You are not authorized to " + action
        );
    }
}
