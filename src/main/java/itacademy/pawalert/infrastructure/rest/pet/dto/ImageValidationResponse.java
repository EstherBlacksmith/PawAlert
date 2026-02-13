package itacademy.pawalert.infrastructure.rest.pet.dto;

import java.util.List;

public record ImageValidationResponse(
        boolean valid,
        String message,

        String species,
        double speciesConfidence,
        String breed,
        double breedConfidence,
        List<String> possibleBreeds,

        String dominantColor,
        String dominantColorHex,

        List<String> visualLabels,

        // Seguridad
        boolean isSafeForWork,
        String safetyMessage
) {
    public static ImageValidationResponse success(
            String species, double speciesConfidence,
            String breed, double breedConfidence,
            List<String> possibleBreeds,
            String dominantColor, String dominantColorHex,
            List<String> visualLabels) {
        return new ImageValidationResponse(
                true,
                "Image validated correctly",
                species,
                speciesConfidence,
                breed,
                breedConfidence,
                possibleBreeds,
                dominantColor,
                dominantColorHex,
                visualLabels,
                true,
                "Image secure"
        );
    }

    public static ImageValidationResponse invalid(String message) {
        return new ImageValidationResponse(
                false,
                message,
                null, 0.0, null, 0.0,
                List.of(),
                null, null,
                List.of(),
                false,
                message
        );
    }
}