package itacademy.pawalert.domain.alert.model;

import itacademy.pawalert.domain.alert.exception.AlertModificationNotAllowedException;
import itacademy.pawalert.infrastructure.persistence.alert.AlertEntity;
import lombok.Getter;

import java.util.UUID;

public class Alert {
    @Getter
    private final UUID id;
    @Getter
    private final UUID petId;
    @Getter
    private final UUID userId;
    @Getter
    private final Title title;
    @Getter
    private final Description description;
    private final StatusAlert statusAlert;

    public Alert(UUID petId, UUID userId, Title title, Description description) {
        this.userId = userId;
        this.id = UUID.randomUUID();
        this.petId = petId;
        this.title = title;
        this.description = description;
        this.statusAlert = new OpenedStateAlert();

    }

    public Alert(UUID id, UUID petId, UUID userId, Title title, Description description) {
        this(id, petId, userId, title, description, new OpenedStateAlert());
    }

    public Alert(UUID id, UUID petId, UUID userId, Title title, Description description, StatusAlert status) {
        this.id = id;
        this.petId = petId;
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.statusAlert = status;
    }

    public AlertEntity toEntity() {
        return new AlertEntity(
                this.id.toString(),
                this.petId.toString(),
                this.userId.toString(),
                this.title.getValue(),
                this.description.getValue(),
                this.statusAlert.getStatusName()
        );
    }

    public StatusAlert currentStatus() {
        return statusAlert;
    }

    public Alert open() {
        return statusAlert.open(this);
    }

    public Alert seen() {
        return statusAlert.seen(this);

    }

    public Alert safe() {
        return statusAlert.safe(this);
    }

    public Alert closed() {
        return statusAlert.closed(this);
    }

    public Alert updateTitle(Title newTitle) {
        StatusNames currentStatus = statusAlert.getStatusName();

        if (currentStatus != StatusNames.OPENED) {
            throw AlertModificationNotAllowedException.cannotModifyTitle(id.toString());
        }

        return new Alert(this.id, this.petId, this.userId, newTitle, this.description, this.statusAlert);
    }

    public Alert updateDescription(Description newDescription) {
        StatusNames currentStatus = statusAlert.getStatusName();

        if (currentStatus != StatusNames.OPENED) {
            throw AlertModificationNotAllowedException.cannotModifyDescription(id.toString());
        }

        return new Alert(this.id, this.petId, this.userId, this.title, newDescription, this.statusAlert);
    }


}
