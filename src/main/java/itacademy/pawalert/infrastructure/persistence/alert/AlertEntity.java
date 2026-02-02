package itacademy.pawalert.infrastructure.persistence.alert;

import itacademy.pawalert.domain.alert.model.*;
import itacademy.pawalert.infrastructure.persistence.pet.PetEntity;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Entity
@Table(name = "alerts")
public class AlertEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id")
    private PetEntity pet;
    private final List<AlertEventEntity> history = new ArrayList<>();
    @Id
    private String id;
    @Column(name = "pet_id")
    private String petId;
    @Column(name = "user_id")
    private String userId;
    @Column(name = "title")
    private String title;
    @Column(name = "description")
    private String description;
    @Column(name = "status")
    private String status;
    @Column(name = "closure_reason")
    private String closureReason;
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Empty constructor required by JPA/Hibernate
    public AlertEntity() {
    }

    // Constructor with StatusNames
    public AlertEntity(String id, String petId, String userId, String title, String description, StatusNames statusNames) {
        this.id = id;
        this.petId = petId;
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.status = statusNames.toString();
        this.createdAt = LocalDateTime.now();
    }

    public Alert toDomain() {

        return new Alert(
                UUID.fromString(this.id),
                UUID.fromString(this.petId),
                new UserId(this.userId),
                new Title(this.title),
                new Description(this.description),
                mapToStatusAlert(StatusNames.valueOf(this.status))
        );
    }

    // This implementation allows to change the status in toDomain method without creating setters
    private StatusAlert mapToStatusAlert(StatusNames status) {

        return switch (status) {
            case OPENED -> new OpenedStateAlert();
            case SEEN -> new SeenStatusAlert();
            case SAFE -> new SafeStatusAlert();
            case CLOSED -> new ClosedStatusAlert();
        };
    }

}
