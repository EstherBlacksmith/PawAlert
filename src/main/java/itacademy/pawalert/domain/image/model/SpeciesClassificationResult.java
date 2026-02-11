package itacademy.pawalert.domain.image.model;

public record SpeciesClassificationResult(
        String species,
        String rawLabel,
        double confidence,
        boolean isRecognized
) {
    public static SpeciesClassificationResult animal(String species,
                                                     double confidence,
                                                     String rawLabel) {
        return new SpeciesClassificationResult(species, rawLabel, confidence, true);
    }

    public static SpeciesClassificationResult unknown() {
        return new SpeciesClassificationResult("Unknown", null, 0.0, false);
    }
}