package itacademy.pawalert.domain.image.model;

public record ImageUploadResult(
        String imageUrl,
        String publicId,
        String aiDescription,
        String safetyStatus,
        int width,
        int height
) {}
