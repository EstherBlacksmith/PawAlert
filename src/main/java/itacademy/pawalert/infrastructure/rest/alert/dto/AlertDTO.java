package itacademy.pawalert.infrastructure.rest.alert.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertDTO {
    private String id;
    private String petId;
    private String userId;
    private String title;
    private String description;
    private String status;
    private Double latitude;
    private Double longitude;
}
