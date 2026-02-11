package itacademy.pawalert.infrastructure.image.google;

import itacademy.pawalert.domain.image.model.*;
import itacademy.pawalert.domain.image.port.inbound.PetImageAnalyzer;
import itacademy.pawalert.domain.image.port.outbound.ImageAnalysisPort;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class GoogleVisionPetAnalyzer implements PetImageAnalyzer {

    private final ImageAnalysisPort imageAnalysisPort;

    private static final Map<String, String> ANIMAL_KEYWORDS = Map.ofEntries(
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
            Map.entry("turtle", "Turtle"),
            Map.entry("lizard", "Lizard"),
            Map.entry("snake", "Snake"),
            Map.entry("fish", "Fish"),
            Map.entry("horse", "Horse"),
            Map.entry("mouse", "Mouse"),
            Map.entry("squirrel", "Squirrel")
    );

    private static final Set<String> KNOWN_BREEDS = Set.of(
            "retriever", "shepherd", "bulldog", "poodle", "beagle", "boxer",
            "husky", "dachshund", "chihuahua", "corgi", "pitbull", "rottweiler",
            "german shepherd", "golden retriever", "labrador", "pomeranian",
            "persian", "siamese", "mainecoon", "tabby", "ragdoll", "bengal",
            "british shorthair", "maine coon", "scottish fold", "sphynx"
    );

    public GoogleVisionPetAnalyzer(ImageAnalysisPort imageAnalysisPort) {
        this.imageAnalysisPort = imageAnalysisPort;
    }

    @Override
    public PetAnalysisResult analyze(byte[] imageBytes) {
        // Ejecutar todos los análisis en paralelo o secuencial
        List<LabelResult> labels = imageAnalysisPort.detectLabels(imageBytes);
        String detectedText = imageAnalysisPort.detectText(imageBytes);
        ColorResult colors = imageAnalysisPort.detectColors(imageBytes);
        SafetyResult safety = imageAnalysisPort.checkSafety(imageBytes);

        SpeciesClassificationResult animalResult = classifyAnimalFromLabels(labels);

        boolean isValidPet = animalResult.isRecognized() && safety.isSafe();

        List<String> possibleBreeds = extractPossibleBreeds(labels);

        String breed = possibleBreeds.isEmpty() ? null : possibleBreeds.get(0);
        double breedConfidence = breed != null ?
                findLabelConfidence(labels, breed) : 0.0;

        return new PetAnalysisResult(
                isValidPet,
                isValidPet ? "Imagen válida de " + animalResult.species() :
                        "No se detectó una mascota válida",
                animalResult.species(),
                animalResult.confidence(),
                breed,
                breedConfidence,
                possibleBreeds,
                colors.dominantColor(),
                colors.hex(),
                List.of(), // Palette completa si la necesitas
                detectedText,
                detectedText != null && !detectedText.isEmpty(),
                labels.stream().map(LabelResult::label).collect(Collectors.toList()),
                mapToSafetyStatus(safety.status()),
                safety.isSafe()
        );
    }

    @Override
    public boolean isPetImage(byte[] imageBytes) {
        SpeciesClassificationResult result = classifyAnimal(imageBytes);
        return result.isRecognized();
    }

    @Override
    public SpeciesClassificationResult classifyAnimal(byte[] imageBytes) {
        List<LabelResult> labels = imageAnalysisPort.detectLabels(imageBytes);
        return classifyAnimalFromLabels(labels);
    }

    private SpeciesClassificationResult classifyAnimalFromLabels(List<LabelResult> labels) {
        double maxConfidence = 0.0;
        String detectedAnimal = null;
        String rawLabel = null;

        for (LabelResult label : labels) {
            String description = label.label().toLowerCase();
            double score = label.score();

            for (Map.Entry<String, String> entry : ANIMAL_KEYWORDS.entrySet()) {
                if (description.contains(entry.getKey())) {
                    if (score > maxConfidence) {
                        maxConfidence = score;
                        detectedAnimal = entry.getValue();
                        rawLabel = label.label();
                    }
                }
            }
        }

        if (detectedAnimal != null && maxConfidence > 0.5) {
            return new SpeciesClassificationResult(detectedAnimal, rawLabel, maxConfidence, true);
        }

        return new SpeciesClassificationResult("Desconocido", null, 0.0, false);
    }

    private List<String> extractPossibleBreeds(List<LabelResult> labels) {
        return labels.stream()
                .map(LabelResult::label)
                .filter(this::isKnownBreed)
                .map(this::capitalize)
                .distinct()
                .collect(Collectors.toList());
    }

    private boolean isKnownBreed(String label) {
        String lower = label.toLowerCase();
        return KNOWN_BREEDS.stream().anyMatch(lower::contains);
    }

    private double findLabelConfidence(List<LabelResult> labels, String breed) {
        return labels.stream()
                .filter(l -> l.label().toLowerCase().contains(breed.toLowerCase()))
                .map(LabelResult::score)
                .findFirst()
                .orElse(0.0);
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    private ContentSafetyStatus mapToSafetyStatus(String safetyStatus) {
        return switch (safetyStatus) {
            case "SAFE" -> ContentSafetyStatus.SAFE;
            case "UNSAFE" -> ContentSafetyStatus.UNSAFE;
            default -> ContentSafetyStatus.QUESTIONABLE;
        };
    }
}
