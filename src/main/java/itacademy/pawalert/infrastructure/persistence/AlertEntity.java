package itacademy.pawalert.infrastructure.persistence;

import itacademy.pawalert.domain.*;
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
    @Id
    private String id;
    @Column(name = "pet_id")
    private String petId;
    @Column(name = "user_id")
    private String userId;
    private String title;
    private String description;
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "alert", cascade = CascadeType.ALL)
    private final List<AlertEventEntity> history = new ArrayList<>();

    // Empty constructor required by JPA/Hibernate
    public AlertEntity() {
    }

    public AlertEntity(String id, String petId, String userId, String title, String description, String status) {
        this.id = id;
        this.petId = petId;
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.status = status;
        this.createdAt = LocalDateTime.now();
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
