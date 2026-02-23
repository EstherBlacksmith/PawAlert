package itacademy.pawalert.domain.image.model;

import java.util.List;

public record PetAnalysisResult(
        boolean isValidPet,
        String validationMessage,

        String species,
        double speciesConfidence,
        String breed,
        double breedConfidence,
        List<String> possibleBreeds,

        String dominantColor,
        String dominantColorHex,
        List<ColorResult> colorPalette,

        String detectedText,
        boolean hasText,

        List<String> visualLabels,
        ContentSafetyStatus safetyStatus,
        boolean isSafeForWork) {

    public static PetAnalysisResult notAPet(String message) {
        return new PetAnalysisResult(false, message, null, 0.0, null, 0.0,
                List.of(), null, null, List.of(), null, false,
                List.of(), ContentSafetyStatus.SAFE, true);
    }
}
