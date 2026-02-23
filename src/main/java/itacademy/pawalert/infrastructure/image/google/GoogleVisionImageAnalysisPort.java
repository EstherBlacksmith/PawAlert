package itacademy.pawalert.infrastructure.image.google;

import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import com.google.type.Color;
import itacademy.pawalert.domain.image.model.*;
import itacademy.pawalert.domain.image.port.outbound.ImageAnalysisPort;
import itacademy.pawalert.domain.image.service.PetColorClassifier;
import itacademy.pawalert.domain.image.service.PetColorClassifier.RGBColor;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class GoogleVisionImageAnalysisPort implements ImageAnalysisPort {

    private static final Logger log = LoggerFactory.getLogger(GoogleVisionImageAnalysisPort.class);

    private final ImageAnnotatorClient imageAnnotatorClient;
    private final PetColorClassifier petColorClassifier;

    public GoogleVisionImageAnalysisPort(
            ImageAnnotatorClient imageAnnotatorClient,
            PetColorClassifier petColorClassifier) {
        this.imageAnnotatorClient = imageAnnotatorClient;
        this.petColorClassifier = petColorClassifier;
    }

    @Override
    public List<LabelResult> detectLabels(byte[] imageBytes) {
        log.debug("Detectando etiquetas en la imagen");

        ByteString byteString = ByteString.copyFrom(imageBytes);
        Image image = Image.newBuilder().setContent(byteString).build();

        Feature labelDetection = Feature.newBuilder()
                .setType(Feature.Type.LABEL_DETECTION)
                .build();

        AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                .addFeatures(labelDetection)
                .setImage(image)
                .build();

        var response = imageAnnotatorClient.batchAnnotateImages(List.of(request))
                .getResponsesList().get(0);

        return response.getLabelAnnotationsList().stream()
                .map(annotation -> new LabelResult(
                        annotation.getDescription(),
                        annotation.getScore()))
                .collect(Collectors.toList());
    }

    @Override
    public String detectText(byte[] imageBytes) {
        log.debug("Detecting test on the image");

        ByteString byteString = ByteString.copyFrom(imageBytes);
        Image image = Image.newBuilder().setContent(byteString).build();

        Feature textDetection = Feature.newBuilder()
                .setType(Feature.Type.TEXT_DETECTION)
                .build();

        AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                .addFeatures(textDetection)
                .setImage(image)
                .build();

        var response = imageAnnotatorClient.batchAnnotateImages(List.of(request))
                .getResponsesList().get(0);

        if (response.hasError() || response.getTextAnnotationsList().isEmpty()) {
            return "";
        }

        return response.getTextAnnotationsList().get(0).getDescription();
    }

    @Override
    public List<DetectedObject> detectObjects(byte[] imageBytes) {
        log.debug("Detecting objects for localization");

        ByteString byteString = ByteString.copyFrom(imageBytes);
        Image image = Image.newBuilder().setContent(byteString).build();

        Feature objectLocalization = Feature.newBuilder()
                .setType(Feature.Type.OBJECT_LOCALIZATION)
                .build();

        AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                .addFeatures(objectLocalization)
                .setImage(image)
                .build();

        try {
            var response = imageAnnotatorClient.batchAnnotateImages(List.of(request))
                    .getResponsesList().get(0);

            if (response.hasError()) {
                log.warn("Error in localization of objets: {}", response.getError().getMessage());
                return List.of();
            }

            return response.getLocalizedObjectAnnotationsList().stream()
                    .map(this::toDetectedObject)
                    .filter(obj -> obj.confidence() > 0.5)
                    .sorted((a, b) -> Double.compare(b.confidence(), a.confidence()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error during the localization of objets", e);
            return List.of();
        }
    }

    private DetectedObject toDetectedObject(LocalizedObjectAnnotation obj) {
        BoundingPoly poly = obj.getBoundingPoly();
        float minX = 1.0f, minY = 1.0f, maxX = 0.0f, maxY = 0.0f;

        for (NormalizedVertex vertex : poly.getNormalizedVerticesList()) {
            minX = Math.min(minX, vertex.getX());
            minY = Math.min(minY, vertex.getY());
            maxX = Math.max(maxX, vertex.getX());
            maxY = Math.max(maxY, vertex.getY());
        }

        BoundingBox bbox = new BoundingBox(minX, minY, maxX, maxY);
        return new DetectedObject(obj.getName(), obj.getScore(), bbox);
    }

    @Override
    public ColorResult detectColors(byte[] imageBytes) {
        log.debug("Detecting colors with pet classification");

        // Locating the pet first
        List<DetectedObject> objects = detectObjects(imageBytes);
        Optional<DetectedObject> petObject = objects.stream()
                .filter(DetectedObject::isPet)
                .findFirst();

        petObject.ifPresent(detectedObject -> log.debug("Pet detected {} with bounding box: {}",
                detectedObject.name(), detectedObject.boundingBox()));

        // Obtaining the principal colors
        ByteString byteString = ByteString.copyFrom(imageBytes);
        Image image = Image.newBuilder().setContent(byteString).build();

        Feature imageProperties = Feature.newBuilder()
                .setType(Feature.Type.IMAGE_PROPERTIES)
                .build();

        AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                .addFeatures(imageProperties)
                .setImage(image)
                .build();

        var response = imageAnnotatorClient.batchAnnotateImages(List.of(request))
                .getResponsesList().get(0);

        var colors = response.getImagePropertiesAnnotation()
                .getDominantColors().getColorsList();

        if (colors.isEmpty()) {
            return new ColorResult("Unknown", "#000000", 0.0);
        }

        // Convert to RGB format  and filter background colors
        List<RGBColor> petColors = colors.stream()
                .map(this::toRGBColor)
                .filter(c -> !petColorClassifier.isLikelyBackground(c.r(), c.g(), c.b(), c.pixelFraction()))
                .filter(c -> !petColorClassifier.isNearWhite(c.r(), c.g(), c.b()))
                .filter(c -> !petColorClassifier.isNearBlack(c.r(), c.g(), c.b()))
                .limit(5)
                .toList();

        if (petColors.isEmpty()) {
            // Fallback to the first color if all colors are filtered
            ColorInfo firstColor = colors.getFirst();
            int r = (int) (firstColor.getColor().getRed() * 255);
            int g = (int) (firstColor.getColor().getGreen() * 255);
            int b = (int) (firstColor.getColor().getBlue() * 255);
            String colorName = petColorClassifier.mapToPetColor(r, g, b);
            return new ColorResult(colorName, petColorClassifier.rgbToHex(r, g, b), firstColor.getScore());
        }

        // Classify colors
        var colorResult = petColorClassifier.classifyMultipleColors(petColors, 3);

        RGBColor primaryRGB = petColors.get(0);
        String hex = petColorClassifier.rgbToHex(primaryRGB.r(), primaryRGB.g(), primaryRGB.b());

        // Mixing colors for multicolor pets
        String displayColor = colorResult.secondaryColor() != null
                ? colorResult.primaryColor() + " and " + colorResult.secondaryColor()
                : colorResult.primaryColor();

        return new ColorResult(displayColor, hex, primaryRGB.score());
    }

    private RGBColor toRGBColor(ColorInfo colorInfo) {
        Color color = colorInfo.getColor();
        return new RGBColor(
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255),
                colorInfo.getScore(),
                colorInfo.getPixelFraction()
        );
    }

    @Override
    public SafetyResult checkSafety(byte[] imageBytes) {
        ByteString byteString = ByteString.copyFrom(imageBytes);
        Image image = Image.newBuilder().setContent(byteString).build();

        Feature safeSearch = Feature.newBuilder()
                .setType(Feature.Type.SAFE_SEARCH_DETECTION)
                .build();

        AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                .addFeatures(safeSearch)
                .setImage(image)
                .build();

        var response = imageAnnotatorClient.batchAnnotateImages(List.of(request))
                .getResponsesList().get(0);

        SafeSearchAnnotation safeAnnotation = response.getSafeSearchAnnotation();

        boolean isSafe = !isLikelyOrHigher(safeAnnotation.getAdult()) &&
                !isLikelyOrHigher(safeAnnotation.getViolence());

        String status = isSafe ? "SAFE" : "UNSAFE";

        return new SafetyResult(isSafe, status);
    }

    private boolean isLikelyOrHigher(Likelihood likelihood) {
        return likelihood == Likelihood.LIKELY ||
                likelihood == Likelihood.VERY_LIKELY;
    }

    @PreDestroy
    public void close() {
        if (imageAnnotatorClient != null) {
            imageAnnotatorClient.close();
        }
    }
}
