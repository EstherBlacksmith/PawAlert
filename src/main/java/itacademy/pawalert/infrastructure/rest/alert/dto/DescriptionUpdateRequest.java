package itacademy.pawalert.infrastructure.rest.alert.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import itacademy.pawalert.domain.alert.model.Description;
import itacademy.pawalert.domain.alert.model.UserId;


public record DescriptionUpdateRequest(UserId userId,Description description) {
    @JsonCreator
    public DescriptionUpdateRequest {}
}
