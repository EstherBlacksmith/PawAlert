package itacademy.pawalert.infrastructure.rest.image.controller;


import itacademy.pawalert.application.image.service.ImageUploadService;
import itacademy.pawalert.application.image.service.ImageValidationService;
import itacademy.pawalert.domain.image.model.ImageValidationResult;
import itacademy.pawalert.domain.image.model.PetAnalysisResult;
import itacademy.pawalert.domain.image.port.inbound.PetImageAnalyzer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/images")
@Tag(name = "Pets", description = "Pet management endpoints for creating, retrieving, and updating pet information")
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
    @Operation(summary = "Validate image", description = "Validates an image to ensure it meets system requirements. Requires authentication.")
    @SecurityRequirement(name = "Bearer JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image validated successfully",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Invalid image"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token missing or invalid")
    })
    public ResponseEntity<ImageValidationResult> validate(
            @Parameter(description = "Image file to validate", required = true)
            @RequestParam MultipartFile file) {
        ImageValidationResult result = validationService.validate(file);
        return ResponseEntity.ok(result);
    }


    @PostMapping("/upload")
    @Operation(summary = "Upload image", description = "Uploads an image to the server in the specified folder. Requires authentication.")
    @SecurityRequirement(name = "Bearer JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image uploaded successfully",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Invalid file or folder not specified"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token missing or invalid")
    })
    public ResponseEntity<String> upload(
            @Parameter(description = "Image file to upload", required = true)
            @RequestParam MultipartFile file,
            @Parameter(description = "Destination folder for the image", required = true)
            @RequestParam String folder) {
        String url = uploadService.upload(file, folder);

        return ResponseEntity.ok(url);
    }

    @PostMapping("/analyze")
    @Operation(summary = "Analyze pet image", description = "Analyzes a pet image using computer vision to detect characteristics. Requires authentication.")
    @SecurityRequirement(name = "Bearer JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Analysis completed successfully",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Invalid image or does not contain a pet"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token missing or invalid")
    })
    public ResponseEntity<PetAnalysisResult> analyze(
            @Parameter(description = "Image file to analyze", required = true)
            @RequestParam MultipartFile file)
            throws IOException {

        return ResponseEntity.ok(
                petImageAnalyzer.analyze(file.getBytes())
        );
    }

    /**
     * Endpoint to classify pet image (dog vs cat)
     * Response format compatible with the frontend
     */
    @PostMapping("/classify")
    @Operation(summary = "Classify pet species", description = "Classifies a pet image as dog, cat, or other species. Requires authentication.")
    @SecurityRequirement(name = "Bearer JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Classification completed successfully",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Error processing the image"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token missing or invalid")
    })
    public ResponseEntity<Map<String, Object>> classify(
            @Parameter(description = "Image file to classify", required = true)
            @RequestParam MultipartFile file) {
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
                response.put("message", "Image classified as " + classification);
            } else {
                response.put("classification", "unknown");
                response.put("confidence", 0.0);
                response.put("message", result.validationMessage() != null ?
                        result.validationMessage() : "No valid pet detected");
            }

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("classification", "error");
            errorResponse.put("confidence", 0.0);
            errorResponse.put("message", "Error processing the image: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

}
