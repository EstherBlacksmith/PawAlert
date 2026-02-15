package itacademy.pawalert.domain.image.model;

public record ImageClassificationResult(
        String classification,
        double confidence,
        String message
) {
    public static ImageClassificationResult dog(double confidence) {
        return new ImageClassificationResult("dog", confidence, "La imagen contiene un perro");
    }

    public static ImageClassificationResult cat(double confidence) {
        return new ImageClassificationResult("cat", confidence, "La imagen contiene un gato");
    }

    public static ImageClassificationResult unknown() {
        return new ImageClassificationResult("unknown", 0.0, "No se detectó perro ni gato en la imagen");
    }
}
