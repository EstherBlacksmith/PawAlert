package itacademy.pawalert.domain.image.model;

public record DetectedObject(
        String name,
        double confidence,
        BoundingBox boundingBox
) {
    public boolean isPet() {
        String lowerName = name.toLowerCase();
        return lowerName.contains("dog") ||
                lowerName.contains("cat") ||
                lowerName.contains("pet") ||
                lowerName.contains("animal") ||
                lowerName.contains("puppy") ||
                lowerName.contains("kitten") ||
                lowerName.contains("bird") ||
                lowerName.contains("rabbit") ||
                lowerName.contains("hamster");
    }
}