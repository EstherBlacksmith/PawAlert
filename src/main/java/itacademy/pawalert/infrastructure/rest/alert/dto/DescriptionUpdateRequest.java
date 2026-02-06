package itacademy.pawalert.infrastructure.rest.alert.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import itacademy.pawalert.domain.alert.model.Description;

import java.util.UUID;


public record DescriptionUpdateRequest(UUID userId, Description description) {
    @JsonCreator
    public DescriptionUpdateRequest {
    }
}
