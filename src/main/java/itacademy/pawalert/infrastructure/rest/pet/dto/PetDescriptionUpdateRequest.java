package itacademy.pawalert.infrastructure.rest.pet.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import itacademy.pawalert.domain.alert.model.Description;
import itacademy.pawalert.domain.alert.model.UserId;

public record PetDescriptionUpdateRequest(UserId userId, Description description) {
    @JsonCreator
    public PetDescriptionUpdateRequest{}
}
