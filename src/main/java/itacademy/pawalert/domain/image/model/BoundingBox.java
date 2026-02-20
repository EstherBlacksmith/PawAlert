package itacademy.pawalert.domain.image.model;


public record BoundingBox(
        float minX,
        float minY,
        float maxX,
        float maxY
) {
    public float width() {
        return maxX - minX;
    }

    public float height() {
        return maxY - minY;
    }

    public float area() {
        return width() * height();
    }

    public boolean isValid() {
        return minX >= 0 && minY >= 0 && maxX <= 1 && maxY <= 1 && area() > 0;
    }
}