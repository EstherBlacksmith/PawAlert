package itacademy.pawalert.domain.image.model;

import java.util.List;

public record ImageTypeResult(
        boolean isPhotograph,
        String detectedType,
        double confidence,
        List<String> typeLabels
) {
    public static ImageTypeResult photograph(List<String> labels) {
        return new ImageTypeResult(true, "photograph", 1.0, labels);
    }

    public static ImageTypeResult nonPhotograph(String detectedType, double confidence, List<String> labels) {
        return new ImageTypeResult(false, detectedType, confidence, labels);
    }
}