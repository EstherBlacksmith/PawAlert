package itacademy.pawalert.infrastructure.rest.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import itacademy.pawalert.domain.Description;
import itacademy.pawalert.domain.UserId;


public record DescriptionUpdateRequest(UserId userId,Description description) {
    @JsonCreator
    public DescriptionUpdateRequest {}
}
