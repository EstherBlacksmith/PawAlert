package itacademy.pawalert.infrastructure.rest.image.controller;


import itacademy.pawalert.application.image.service.ImageUploadService;
import itacademy.pawalert.application.image.service.ImageValidationService;
import itacademy.pawalert.domain.image.model.ImageClassificationResult;
import itacademy.pawalert.domain.image.model.ImageValidationResult;
import itacademy.pawalert.domain.image.model.PetAnalysisResult;
import itacademy.pawalert.domain.image.port.inbound.PetImageAnalyzer;
import itacademy.pawalert.infrastructure.image.google.GoogleVisionService;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/images")
public class ImageController {
    private final ImageValidationService validationService;
    private final ImageUploadService uploadService;
    private final PetImageAnalyzer petImageAnalyzer;

    public ImageController(ImageValidationService validationService,
                           ImageUploadService uploadService, PetImageAnalyzer petImageAnalyzer) {
        this.validationService = validationService;
        this.uploadService = uploadService;
        this.petImageAnalyzer = petImageAnalyzer;
    }

    @PostMapping("/validate")
    public ResponseEntity<ImageValidationResult> validate(@RequestParam MultipartFile file) {
        ImageValidationResult result = validationService.validate(file);
        return ResponseEntity.ok(result);
    }


    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam MultipartFile file,
                                         @RequestParam String folder) {
        String url = uploadService.upload(file, folder);

         return ResponseEntity.ok(url);
    }

    @PostMapping("/analyze")
    public ResponseEntity<PetAnalysisResult> analyze(@RequestParam MultipartFile file)
            throws IOException {

        return ResponseEntity.ok(
                petImageAnalyzer.analyze(file.getBytes())
        );
    }

    /**
     * Endpoint para clasificar imagen de mascota (perro vs gato)
     * Formato de respuesta compatible con el frontend
     */
    @PostMapping("/classify")
    public ResponseEntity<Map<String, Object>> classify(@RequestParam MultipartFile file) {
        try {
            PetAnalysisResult result = petImageAnalyzer.analyze(file.getBytes());
            
            Map<String, Object> response = new HashMap<>();
            
            if (result.isValidPet() && result.species() != null) {
                String species = result.species().toLowerCase();
                String classification;
                
                if (species.contains("dog") || species.contains("canine")) {
                    classification = "dog";
                } else if (species.contains("cat") || species.contains("feline")) {
                    classification = "cat";
                } else {
                    classification = "unknown";
                }
                
                response.put("classification", classification);
                response.put("confidence", result.speciesConfidence());
                response.put("message", "Imagen clasificada como " + classification);
            } else {
                response.put("classification", "unknown");
                response.put("confidence", 0.0);
                response.put("message", result.validationMessage() != null ? 
                    result.validationMessage() : "No se detectó una mascota válida");
            }
            
            return ResponseEntity.ok(response);
            
        } catch (IOException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("classification", "error");
            errorResponse.put("confidence", 0.0);
            errorResponse.put("message", "Error al procesar la imagen: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

}
