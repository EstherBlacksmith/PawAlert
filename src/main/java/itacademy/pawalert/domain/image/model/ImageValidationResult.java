package itacademy.pawalert.domain.image.model;

import java.util.List;

public record ImageValidationResult(
        boolean isValid,
        ContentSafetyStatus safetyStatus,
        String aiDescription,
        List<String> detectedLabels,
        String moderationReason
) {
    //Result for safe image
    public static ImageValidationResult safe(String description, List<String> labels) {
        return new ImageValidationResult(true, ContentSafetyStatus.SAFE, description, labels, null);
    }

    //Result for unsafe image
    public static ImageValidationResult unsafe(String reason) {
        return new ImageValidationResult(false, ContentSafetyStatus.UNSAFE, null, List.of(), reason);
    }
}
