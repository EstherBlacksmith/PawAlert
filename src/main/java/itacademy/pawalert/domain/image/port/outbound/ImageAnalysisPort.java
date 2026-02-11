package itacademy.pawalert.domain.image.port.outbound;

import itacademy.pawalert.domain.image.model.ColorResult;
import itacademy.pawalert.domain.image.model.LabelResult;
import itacademy.pawalert.domain.image.model.SafetyResult;

import java.util.List;

public interface ImageAnalysisPort {
    List<LabelResult> detectLabels(byte[] imageBytes);
    String detectText(byte[] imageBytes);
    ColorResult detectColors(byte[] imageBytes);
    SafetyResult checkSafety(byte[] imageBytes);
}

