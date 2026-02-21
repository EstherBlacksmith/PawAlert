package itacademy.pawalert.infrastructure.persistence.alert;

import itacademy.pawalert.domain.alert.model.*;
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
    @Column(name = "id")
    private String id;

    @Column(name = "pet_id")
    private String petId;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "title")
    private String title;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "status")
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "alert", cascade = CascadeType.ALL)
    private final List<AlertEventEntity> events = new ArrayList<>();


    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // Empty constructor required by JPA/Hibernate
    public AlertEntity() {
    }

    // Constructor with String petId (for domain-to-entity conversion)
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
                UUID.fromString(this.userId),
                Title.of(this.title),
                Description.of(this.description),
                mapToStatusAlert(StatusNames.valueOf(this.status))
        );
    }

    private StatusAlert mapToStatusAlert(StatusNames status) {
        return switch (status) {
            case OPENED -> new OpenedStateAlert();
            case SEEN -> new SeenStatusAlert();
            case SAFE -> new SafeStatusAlert();
            case CLOSED -> new ClosedStatusAlert();
        };
    }
}
