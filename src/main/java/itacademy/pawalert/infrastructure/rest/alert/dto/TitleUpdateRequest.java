package itacademy.pawalert.infrastructure.rest.alert.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import itacademy.pawalert.domain.alert.model.Title;
import itacademy.pawalert.domain.alert.model.UserId;


public record TitleUpdateRequest(UserId userId, Title title) {
    @JsonCreator
    public TitleUpdateRequest {}

}
