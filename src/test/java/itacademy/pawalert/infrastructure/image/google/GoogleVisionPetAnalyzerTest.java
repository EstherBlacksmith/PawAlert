package itacademy.pawalert.infrastructure.image.google;

import itacademy.pawalert.domain.image.model.*;
import itacademy.pawalert.domain.image.port.inbound.PetImageAnalyzer;
import itacademy.pawalert.domain.image.port.outbound.ImageAnalysisPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoogleVisionPetAnalyzerTest {

    @Mock
    private ImageAnalysisPort imageAnalysisPort;

    private PetImageAnalyzer petImageAnalyzer;

    @BeforeEach
    void setUp() {
        petImageAnalyzer = new GoogleVisionPetAnalyzer(imageAnalysisPort);
    }

    @Test
    void analyze_dogImage_returnsValidPetResult() {
        // Given
        byte[] imageBytes = "fake image bytes".getBytes();

        List<LabelResult> labels = List.of(
                new LabelResult("Dog", 0.95),
                new LabelResult("Golden Retriever", 0.90),
                new LabelResult("Pet", 0.85)
        );

        when(imageAnalysisPort.detectLabels(imageBytes)).thenReturn(labels);
        when(imageAnalysisPort.detectText(imageBytes)).thenReturn("");
        when(imageAnalysisPort.detectColors(imageBytes))
                .thenReturn(new ColorResult("Brown", "#A52A2A", 0.8));
        when(imageAnalysisPort.checkSafety(imageBytes))
                .thenReturn(new SafetyResult(true, "SAFE"));

        // When
        PetAnalysisResult result = petImageAnalyzer.analyze(imageBytes);

        // Then
        assertTrue(result.isValidPet());
        assertEquals("Dog", result.species());
        assertEquals("Golden Retriever", result.breed());
        assertTrue(result.isSafeForWork());
        verify(imageAnalysisPort).detectLabels(imageBytes);
        verify(imageAnalysisPort).detectText(imageBytes);
        verify(imageAnalysisPort).detectColors(imageBytes);
        verify(imageAnalysisPort).checkSafety(imageBytes);
    }

    @Test
    void analyze_nonPetImage_returnsNotValid() {
        // Given
        byte[] imageBytes = "fake image bytes".getBytes();

        List<LabelResult> labels = List.of(
                new LabelResult("Car", 0.95),
                new LabelResult("Road", 0.90)
        );

        when(imageAnalysisPort.detectLabels(imageBytes)).thenReturn(labels);

        // When
        PetAnalysisResult result = petImageAnalyzer.analyze(imageBytes);

        // Then
        assertFalse(result.isValidPet());
        assertEquals("Desconocido", result.species());
    }

    @Test
    void classifyAnimal_catImage_returnsCat() {
        // Given
        byte[] imageBytes = "fake cat image".getBytes();

        List<LabelResult> labels = List.of(
                new LabelResult("Cat", 0.92),
                new LabelResult("Siamese", 0.85)
        );

        when(imageAnalysisPort.detectLabels(imageBytes)).thenReturn(labels);

        // When
        SpeciesClassificationResult result = petImageAnalyzer.classifyAnimal(imageBytes);

        // Then
        assertEquals("Cat", result.species());
        assertTrue(result.isRecognized());
        assertEquals(0.92, result.confidence());
    }

    @Test
    void isPetImage_dogImage_returnsTrue() {
        // Given
        byte[] imageBytes = "fake dog image".getBytes();

        List<LabelResult> labels = List.of(
                new LabelResult("Dog", 0.95)
        );

        when(imageAnalysisPort.detectLabels(imageBytes)).thenReturn(labels);

        // When
        boolean result = petImageAnalyzer.isPetImage(imageBytes);

        // Then
        assertTrue(result);
    }

    @Test
    void isPetImage_carImage_returnsFalse() {
        // Given
        byte[] imageBytes = "fake car image".getBytes();

        List<LabelResult> labels = List.of(
                new LabelResult("Car", 0.95)
        );

        when(imageAnalysisPort.detectLabels(imageBytes)).thenReturn(labels);

        // When
        boolean result = petImageAnalyzer.isPetImage(imageBytes);

        // Then
        assertFalse(result);
    }
}
