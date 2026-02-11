package itacademy.pawalert.infrastructure.image.google;

import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import itacademy.pawalert.domain.image.model.ContentSafetyStatus;
import itacademy.pawalert.domain.image.model.ImageValidationResult;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;
import java.util.stream.Collectors;



import java.util.List;

@Service
public class GoogleVisionService {

    private final ImageAnnotatorClient imageAnnotatorClient;

    public GoogleVisionService(ImageAnnotatorClient imageAnnotatorClient) {
        this.imageAnnotatorClient = imageAnnotatorClient;
    }

    public ImageValidationResult analyzeImage(byte[] imageBytes){
        //Bytes to google format
        ByteString byteString = ByteString.copyFrom(imageBytes);
        Image image = Image.newBuilder().setContent(byteString).build();

        //Configure the analysis:Labels + Safe search
        Feature localDetection = Feature.newBuilder()
                .setType(Feature.Type.LABEL_DETECTION)
                .build();

        Feature safeSearch = Feature.newBuilder().
                setType(Feature.Type.SAFE_SEARCH_DETECTION)
                .build();

        //Create the request
        AnnotateImageRequest annotateImageRequest = AnnotateImageRequest.newBuilder()
                .addFeatures(localDetection)
                .addFeatures(safeSearch)
                .setImage(image)
                .build();

        //Execute the analysis
        List<AnnotateImageRequest> requests = List.of(annotateImageRequest);
        var batchResponse = imageAnnotatorClient.batchAnnotateImages(requests);

        var response = batchResponse.getResponsesList().get(0);

        //Process labels
        List<String> labels = response.getLabelAnnotationsList().stream()
                .map(EntityAnnotation::getDescription)
                .collect(Collectors.toList());

        String description = generateBasicDescription(labels);
        SafeSearchAnnotation safeAnnotation = response.getSafeSearchAnnotation();
        ContentSafetyStatus safetyStatus = evaluateSafeSearch(safeAnnotation);

        return new ImageValidationResult(
                safetyStatus != ContentSafetyStatus.UNSAFE,
                safetyStatus,
                description,
                labels,
                safetyStatus.name()
        );
    }

    private ContentSafetyStatus evaluateSafeSearch(SafeSearchAnnotation safe){
        if(isLikelyOrHigher(safe.getAdult()) ||
                isLikelyOrHigher(safe.getViolence())){
            return ContentSafetyStatus.UNSAFE;
        }

        if(isPossibleOrHigher(safe.getAdult()) ||
                isPossibleOrHigher(safe.getRacy())){
            return ContentSafetyStatus.QUESTIONABLE;
        }

        return ContentSafetyStatus.SAFE;
    }

    private boolean isLikelyOrHigher(Likelihood  value){
        return value == Likelihood.LIKELY || value == Likelihood.VERY_LIKELY;
    }

    private boolean isPossibleOrHigher(Likelihood  value){
        return value == Likelihood.POSSIBLE ||
                value == Likelihood.LIKELY ||
                value == Likelihood.VERY_LIKELY;
    }

    private String generateBasicDescription(List<String> labels){
        if(labels.isEmpty()){
            return "There are not elements detected on the image";
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Image of ");

        //First labels
        for (int i = 0; i <Math.min(3,labels.size()) ; i++) {
            if (i < 0) {
                stringBuilder.append(", ");
            }
            stringBuilder.append(labels.get(i));
        }
        return  stringBuilder.toString();
    }

    @PreDestroy
    public void close() {
        if (imageAnnotatorClient != null) {
            imageAnnotatorClient.close();
        }
    }
}
