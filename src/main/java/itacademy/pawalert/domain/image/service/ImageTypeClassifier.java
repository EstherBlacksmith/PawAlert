package itacademy.pawalert.domain.image.service;

import itacademy.pawalert.domain.image.model.ImageTypeResult;
import itacademy.pawalert.domain.image.model.LabelResult;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;


@Component
public class ImageTypeClassifier {

    private static final Set<String> ART_INDICATOR_LABELS = Set.of(
            "drawing", "illustration", "sketch", "cartoon", "clip art",
            "art", "painting", "artwork", "animation", "digital art",
            "vector graphics", "graphic design", "clipart", "line art",
            "doodle", "caricature"
    );

    private static final double REJECTION_CONFIDENCE_THRESHOLD = 0.6;

    public ImageTypeResult classify(List<LabelResult> labels) {
        List<String> labelNames = labels.stream()
                .map(LabelResult::label)
                .toList();

        for (LabelResult label : labels) {
            if (isArtIndicator(label)) {
                return ImageTypeResult.nonPhotograph(
                        label.label().toLowerCase(),
                        label.score(),
                        labelNames
                );
            }
        }

        return ImageTypeResult.photograph(labelNames);
    }

    private boolean isArtIndicator(LabelResult label) {
        String lowerLabel = label.label().toLowerCase();
        boolean containsArtLabel = ART_INDICATOR_LABELS.stream()
                .anyMatch(lowerLabel::contains);
        boolean hasHighConfidence = label.score() > REJECTION_CONFIDENCE_THRESHOLD;
        return containsArtLabel && hasHighConfidence;
    }
}
