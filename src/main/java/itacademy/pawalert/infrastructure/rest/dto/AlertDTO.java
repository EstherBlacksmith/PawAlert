package itacademy.pawalert.infrastructure.rest.dto;

import lombok.Getter;

@Getter
public class AlertDTO {

    private String petId;
    private String tittle;
    private String description;

    public String getUserId() {
        return "userID_pruebas";
    }
}
