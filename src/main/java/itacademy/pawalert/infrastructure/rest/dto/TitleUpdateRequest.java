package itacademy.pawalert.infrastructure.rest.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import itacademy.pawalert.domain.Title;
import itacademy.pawalert.domain.UserId;


public record TitleUpdateRequest(UserId userId, Title title) {
    @JsonCreator
    public TitleUpdateRequest {}

}
