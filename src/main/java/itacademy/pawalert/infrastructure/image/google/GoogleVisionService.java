package itacademy.pawalert.infrastructure.image.google;

import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import com.google.type.Color;
import itacademy.pawalert.domain.image.model.ContentSafetyStatus;
import itacademy.pawalert.domain.image.model.ImageValidationResult;
import itacademy.pawalert.domain.image.model.SpeciesClassificationResult;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;

import java.util.Map;
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

    /**
     * Classify image as dog or cat based on labels detected by Google Vision
     * @param imageBytes the image bytes to analyze
     * @return ImageClassificationResult with classification (dog/cat) and confidence level
     */
    public SpeciesClassificationResult classifyAnimal(byte[] imageBytes) {
        ByteString byteString = ByteString.copyFrom(imageBytes);
        Image image = Image.newBuilder().setContent(byteString).build();

        Feature labelDetection = Feature.newBuilder()
                .setType(Feature.Type.LABEL_DETECTION)
                .build();

        AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                .addFeatures(labelDetection)
                .setImage(image)
                .build();

        var batchResponse = imageAnnotatorClient.batchAnnotateImages(List.of(request));
        var response = batchResponse.getResponsesList().get(0);

        List<EntityAnnotation> annotations = response.getLabelAnnotationsList();

        Map<String, String> animalKeywords = Map.ofEntries(
                Map.entry("dog", "Dog"),
                Map.entry("puppy", "Dog"),
                Map.entry("canine", "Dog"),
                Map.entry("cat", "Cat"),
                Map.entry("kitten", "Cat"),
                Map.entry("feline", "Cat"),
                Map.entry("bird", "Bird"),
                Map.entry("parrot", "Parrot"),
                Map.entry("eagle", "Eagle"),
                Map.entry("rabbit", "Rabbit"),
                Map.entry("hamster", "Hamster"),
                Map.entry("guinea pig", "Guinea Pig"),
                Map.entry("mouse", "Mouse"),
                Map.entry("turtle", "Turtle"),
                Map.entry("lizard", "Lizard"),
                Map.entry("snake", "Snake"),
                Map.entry("fish", "Fish"),
                Map.entry("horse", "Horse"),
                Map .entry("cow","Cow"),
                Map.entry("sheep", "Sheep"),
                Map.entry("goat", "Goat"),
                Map.entry("pig", "Pig"),
                Map.entry("duck", "Duck"),
                Map.entry("chicken", "Chicken"),
                Map.entry("squirrel", "Squirrel"),
                Map.entry("deer", "Deer"),
                Map.entry("fox", "Fox"),
                Map.entry("wolf", "Wolf"),
                Map.entry("bear", "Bear"),
                Map.entry("lion", "Lion"),
                Map.entry("tiger", "Tiger"),
                Map.entry("elephant", "Elephant"),
                Map.entry("monkey", "Monkey"),
                Map.entry("gorilla", "Gorilla")
        );


        double maxConfidence = 0.0;
        String detectedAnimal = null;
        String rawLabel = null;

        for (EntityAnnotation annotation : annotations) {
            String description = annotation.getDescription().toLowerCase();
            double score = annotation.getScore();

            // Buscar coincidencias en el mapa
            for (Map.Entry<String, String> entry : animalKeywords.entrySet()) {
                if (description.contains(entry.getKey())) {
                    if (score > maxConfidence) {
                        maxConfidence = score;
                        detectedAnimal = entry.getValue();
                        rawLabel = annotation.getDescription();
                    }
                }
            }
        }

        if (detectedAnimal != null && maxConfidence > 0.5) {
            return SpeciesClassificationResult.animal(detectedAnimal, maxConfidence, rawLabel);
        } else {
            return SpeciesClassificationResult.unknown();
        }
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

        if (response.hasError()) {
            return "";
        }

        var texts = response.getTextAnnotationsList();
        if (texts.isEmpty()) {
            return "";
        }

        // Thew first result is the image, the rest are individual words
        return texts.get(0).getDescription();
    }

    public String getDominantColor(byte[] imageBytes) {
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
            return "Desconocido";
        }

        // Returns the most common color
        ColorInfo topColor = colors.get(0);
        return getColorName(topColor.getColor());
    }

    private String getColorName(Color color) {
        int red = (int) (color.getRed() * 255);
        int green = (int) (color.getGreen() * 255);
        int blue = (int) (color.getBlue() * 255);

        // Mapeo simple de RGB a nombres de color
        if (red > 200 && green > 200 && blue > 200) return "Blanco";
        if (red < 50 && green < 50 && blue < 50) return "Negro";
        if (red > green && red > blue) return "Marrón/Rojo";
        if (green > red && green > blue) return "Verde";
        if (blue > red && blue > green) return "Azul";
        if (red > 150 && green > 100 && blue < 100) return "Marrón";
        if (red > 200 && green > 150 && blue < 50) return "Naranja/Crema";

        return "Otro";
    }
}
