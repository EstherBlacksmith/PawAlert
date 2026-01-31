package itacademy.pawalert.infrastructure.persistence;

import itacademy.pawalert.domain.Alert;
import itacademy.pawalert.domain.Description;
import itacademy.pawalert.domain.StatusNames;
import itacademy.pawalert.domain.Tittle;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@Table(name = "alerts")
public class AlertEntity {
    @Id
    private String id;
    private String petId;
    private String title;
    private String description;
    private String status;
    private LocalDateTime createdAt;

    // Empty constructor required by JPA/Hibernate
    public AlertEntity() {}

    public AlertEntity(String id, String petId, String title, String description, String status) {
        this.id = id;
        this.petId = petId;
        this.title = title;
        this.description = description;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }

    public AlertEntity(String id, String petId, String title, String description, StatusNames statusNames) {
        this.id = id;
        this.petId = petId;
        this.title = title;
        this.description = description;
        this.status = statusNames.toString();
        this.createdAt = LocalDateTime.now();
    }

    public Alert toDomain() {
        return new Alert(
                UUID.fromString(this.petId),
                new Tittle(this.title),
                new Description(this.description)
        );
    }



}
