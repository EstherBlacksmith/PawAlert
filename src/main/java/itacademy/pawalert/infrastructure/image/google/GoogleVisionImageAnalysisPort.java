package itacademy.pawalert.infrastructure.image.google;
import com.google.type.Color;
import com.google.cloud.vision.v1.ColorInfo;
import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import itacademy.pawalert.domain.image.model.ColorResult;
import itacademy.pawalert.domain.image.model.LabelResult;
import itacademy.pawalert.domain.image.model.SafetyResult;
import itacademy.pawalert.domain.image.port.outbound.ImageAnalysisPort;

import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class GoogleVisionImageAnalysisPort implements ImageAnalysisPort {

    private final ImageAnnotatorClient imageAnnotatorClient;

    public GoogleVisionImageAnalysisPort(ImageAnnotatorClient imageAnnotatorClient) {
        this.imageAnnotatorClient = imageAnnotatorClient;
    }

    @Override
    public List<LabelResult> detectLabels(byte[] imageBytes) {
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
    public ColorResult detectColors(byte[] imageBytes) {
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
            return new ColorResult("Desconocido", "#000000", 0.0);
        }

        var topColor = colors.get(0);
        String colorName = getColorName(topColor);
        String hex = rgbToHex(topColor);

        return new ColorResult(colorName, hex, topColor.getScore());

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

    private String getColorName(ColorInfo  colorInfo) {
        Color color = colorInfo.getColor();
        float red = color.getRed();
        float green = color.getGreen();
        float blue = color.getBlue();

        int r = (int) (red * 255);
        int g = (int) (green * 255);
        int b = (int) (blue * 255);

        if (r > 200 && g > 200 && b > 200) return "White";
        if (r < 50 && g < 50 && b < 50) return "Black";
        if (r > 150 && g > 150 && b < 100) return "Yellow";
        if (r > 200 && g > 100 && g < 200 && b < 50) return "Orange";
        if (r > 100 && r < 200 && g > 50 && g < 150 && b < 50) return "Brown";
        if (r > 150 && g < 100 && b < 100) return "Red";
        if (r >= g && r >= b) return "Reddish";
        if (g >= r && g >= b) return "Green";
        if (b >= r && b >= g) return "Blue";

        return "Other";
    }

    private String rgbToHex(ColorInfo colorInfo) {
        Color color = colorInfo.getColor();
        int r = (int) (color.getRed() * 255);
        int g = (int) (color.getGreen() * 255);
        int b = (int) (color.getBlue() * 255);
        return String.format("#%02X%02X%02X", r, g, b);
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