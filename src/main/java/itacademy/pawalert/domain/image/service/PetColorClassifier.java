package itacademy.pawalert.domain.image.service;

import itacademy.pawalert.domain.image.model.ColorResult;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class PetColorClassifier {

    // Definiciones de colores de mascotas con rangos RGB
    private record PetColorDefinition(
            String name,
            int minR, int minG, int minB,
            int maxR, int maxG, int maxB
    ) {
        public double distanceFrom(int r, int g, int b) {
            int centerR = (minR + maxR) / 2;
            int centerG = (minG + maxG) / 2;
            int centerB = (minB + maxB) / 2;
            return Math.sqrt(
                    Math.pow(r - centerR, 2) +
                            Math.pow(g - centerG, 2) +
                            Math.pow(b - centerB, 2)
            );
        }
    }

    private static final List<PetColorDefinition> PET_COLORS = List.of(
            new PetColorDefinition("Black", 0, 0, 0, 60, 60, 60),
            new PetColorDefinition("White", 230, 230, 230, 255, 255, 255),
            new PetColorDefinition("Brown", 100, 50, 0, 180, 120, 80),
            new PetColorDefinition("Tan", 180, 140, 80, 220, 190, 140),
            new PetColorDefinition("Cream", 220, 200, 160, 255, 245, 220),
            new PetColorDefinition("Grey", 80, 80, 80, 180, 180, 180),
            new PetColorDefinition("Golden", 180, 140, 40, 220, 180, 80),
            new PetColorDefinition("Ginger", 180, 80, 40, 220, 120, 60),
            new PetColorDefinition("Red", 150, 50, 30, 200, 90, 60),
            new PetColorDefinition("Sable", 120, 70, 30, 180, 130, 70),
            new PetColorDefinition("Brindle", 80, 50, 30, 140, 100, 70)
    );

    private static final Set<String> BACKGROUND_HINTS = Set.of("white", "cream", "grey");

    public record RGBColor(int r, int g, int b, double score, double pixelFraction) {}

    public record ClassificationResult(
            String primaryColor,
            String secondaryColor,
            List<String> allColors
    ) {}

    public String mapToPetColor(int r, int g, int b) {
        String closestColor = "Unknown";
        double minDistance = Double.MAX_VALUE;

        for (PetColorDefinition colorDef : PET_COLORS) {
            double distance = colorDef.distanceFrom(r, g, b);
            if (distance < minDistance) {
                minDistance = distance;
                closestColor = colorDef.name();
            }
        }

        return closestColor;
    }

    public ClassificationResult classifyMultipleColors(List<RGBColor> colors, int maxColors) {
        if (colors.isEmpty()) {
            return new ClassificationResult("Unknown", null, List.of());
        }

        List<String> mappedColors = colors.stream()
                .limit(maxColors)
                .map(c -> mapToPetColor(c.r(), c.g(), c.b()))
                .distinct()
                .toList();

        String primaryColor = mappedColors.get(0);
        String secondaryColor = mappedColors.size() > 1 ? mappedColors.get(1) : null;

        return new ClassificationResult(primaryColor, secondaryColor, mappedColors);
    }

    public boolean isLikelyBackground(int r, int g, int b, double pixelFraction) {
        // Colores muy claros con baja fracción de píxeles son probablemente fondo
        if (r > 240 && g > 240 && b > 240 && pixelFraction < 0.3) {
            return true;
        }
        // Gris muy neutro con baja fracción de píxeles
        int avg = (r + g + b) / 3;
        int variance = Math.abs(r - avg) + Math.abs(g - avg) + Math.abs(b - avg);
        return variance < 30 && pixelFraction < 0.2;
    }

    public boolean isNearWhite(int r, int g, int b) {
        return r > 240 && g > 240 && b > 240;
    }

    public boolean isNearBlack(int r, int g, int b) {
        return r < 30 && g < 30 && b < 30;
    }

    public String rgbToHex(int r, int g, int b) {
        return String.format("#%02X%02X%02X", r, g, b);
    }
}
