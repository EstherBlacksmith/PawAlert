package itacademy.pawalert.domain.image.port.inbound;

import itacademy.pawalert.domain.image.model.PetAnalysisResult;
import itacademy.pawalert.domain.image.model.SpeciesClassificationResult;

public interface PetImageAnalyzer {
    PetAnalysisResult analyze(byte[] imageBytes);

    boolean isPetImage(byte[] imageBytes);

    SpeciesClassificationResult classifyAnimal(byte[] imageBytes);

}


