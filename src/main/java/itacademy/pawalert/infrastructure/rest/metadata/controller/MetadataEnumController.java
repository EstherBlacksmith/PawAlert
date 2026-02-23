package itacademy.pawalert.infrastructure.rest.metadata.controller;

import itacademy.pawalert.application.metadata.service.MetadataEnumService;
import itacademy.pawalert.infrastructure.rest.metadata.dto.MetadataEnumListDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/v1/metadata", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Metadata", description = "Metadata endpoints for retrieving system information and reference data")
public class MetadataEnumController {

    private final MetadataEnumService metadataEnumService;

    @GetMapping("/get-metadata")
    @Operation(summary = "Obtener metadatos del sistema", description = "Recupera información de metadatos del sistema incluyendo enumeraciones y datos de referencia. Este es un endpoint público que no requiere autenticación.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Metadatos recuperados exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MetadataEnumListDto.class)))
    })
    public ResponseEntity<List<MetadataEnumListDto>> getMetadata() {
        List<MetadataEnumListDto> metadata = metadataEnumService.getMetadataEnum();
        return ResponseEntity.ok(metadata);
    }
}