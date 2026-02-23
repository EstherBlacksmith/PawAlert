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
    @Operation(summary = "Validar imagen", description = "Valida una imagen para asegurar que cumple con los requisitos del sistema. Requiere autenticación.")
    @SecurityRequirement(name = "Bearer JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Imagen validada exitosamente",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Imagen inválida"),
            @ApiResponse(responseCode = "401", description = "No autorizado - Token JWT faltante o inválido")
    })
    public ResponseEntity<ImageValidationResult> validate(
            @Parameter(description = "Archivo de imagen a validar", required = true)
            @RequestParam MultipartFile file) {
        ImageValidationResult result = validationService.validate(file);
        return ResponseEntity.ok(result);
    }


    @PostMapping("/upload")
    @Operation(summary = "Cargar imagen", description = "Carga una imagen al servidor en la carpeta especificada. Requiere autenticación.")
    @SecurityRequirement(name = "Bearer JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Imagen cargada exitosamente",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Archivo inválido o carpeta no especificada"),
            @ApiResponse(responseCode = "401", description = "No autorizado - Token JWT faltante o inválido")
    })
    public ResponseEntity<String> upload(
            @Parameter(description = "Archivo de imagen a cargar", required = true)
            @RequestParam MultipartFile file,
            @Parameter(description = "Carpeta de destino para la imagen", required = true)
            @RequestParam String folder) {
        String url = uploadService.upload(file, folder);

        return ResponseEntity.ok(url);
    }

    @PostMapping("/analyze")
    @Operation(summary = "Analizar imagen de mascota", description = "Analiza una imagen de mascota usando visión por computadora para detectar características. Requiere autenticación.")
    @SecurityRequirement(name = "Bearer JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Análisis completado exitosamente",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Imagen inválida o no contiene una mascota"),
            @ApiResponse(responseCode = "401", description = "No autorizado - Token JWT faltante o inválido")
    })
    public ResponseEntity<PetAnalysisResult> analyze(
            @Parameter(description = "Archivo de imagen a analizar", required = true)
            @RequestParam MultipartFile file)
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
    @Operation(summary = "Clasificar especie de mascota", description = "Clasifica una imagen de mascota como perro, gato u otra especie. Requiere autenticación.")
    @SecurityRequirement(name = "Bearer JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Clasificación completada exitosamente",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Error al procesar la imagen"),
            @ApiResponse(responseCode = "401", description = "No autorizado - Token JWT faltante o inválido")
    })
    public ResponseEntity<Map<String, Object>> classify(
            @Parameter(description = "Archivo de imagen a clasificar", required = true)
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
