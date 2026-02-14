package itacademy.pawalert.infrastructure.persistence.alert;

import itacademy.pawalert.domain.alert.model.*;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@Table(name = "alerts")
public class AlertEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "pet_id")
    private UUID petId;

    @Column(name = "user_id")
    private UUID userId;

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

    // Constructor with UUID (for domain-to-entity conversion)
    public AlertEntity(UUID id, UUID petId, UUID userId, String title, String description, StatusNames statusNames) {
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
                this.id,
                this.petId,
                this.userId,
                Title.of(this.title),
                Description.of(this.description),
                mapToStatusAlert(StatusNames.fromString(this.status))
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
