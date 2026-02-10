package itacademy.pawalert.domain.image.exception;

import itacademy.pawalert.domain.image.model.ContentSafetyStatus;
import lombok.Getter;

@Getter
public class ImageValidationException extends RuntimeException {
    private final ContentSafetyStatus safetyStatus;

    public ImageValidationException(String message, ContentSafetyStatus safetyStatus) {
        super(message);
        this.safetyStatus = safetyStatus;
    }

}
