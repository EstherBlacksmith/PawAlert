package itacademy.pawalert.infrastructure.persistence;

import itacademy.pawalert.domain.StatusNames;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "alerts")
public class AlertEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private final Long petId;
    private final String title;
    private final String description;
    private final String status;
    private final LocalDateTime createdAt;


    public AlertEntity(Long petId, String title, String description, String status) {
        this.petId = petId;
        this.title = title;
        this.description = description;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }

    public AlertEntity(Long petId, String title, String description, StatusNames statusNames) {
        this.petId = petId;
        this.title = title;
        this.description = description;
        this.status = statusNames.toString();
        this.createdAt = LocalDateTime.now();
    }
}
