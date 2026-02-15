package itacademy.pawalert.infrastructure.rest.alert.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import itacademy.pawalert.domain.alert.model.Title;

import java.util.UUID;


public record TitleUpdateRequest(UUID userId, Title title) {
    @JsonCreator
    public TitleUpdateRequest {
    }

}
