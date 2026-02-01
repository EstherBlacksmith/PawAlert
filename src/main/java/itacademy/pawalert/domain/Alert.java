package itacademy.pawalert.domain;

import itacademy.pawalert.infrastructure.persistence.AlertEntity;
import lombok.Getter;

import java.util.UUID;

public class Alert {
    @Getter
    private final UUID id;
    @Getter
    private final UUID petId;
    @Getter
    private final UserId userID;
    @Getter
    private Title title;
    @Getter
    private Description description;
    private StatusAlert statusAlert;

    public Alert(UUID petId, UserId userID, Title title, Description description) {
        this.userID = userID;
        this.id = UUID.randomUUID();
        this.petId = petId;
        this.title = title;
        this.description = description;
        this.statusAlert = new OpenedStateAlert();

    }

    public Alert(UUID id, UUID petId, UserId userID, Title title, Description description) {
        this.id = id;
        this.petId = petId;
        this.userID = userID;
        this.title = title;
        this.description = description;
        this.statusAlert = new OpenedStateAlert();
    }

    public Alert(UUID id, UUID petId, UserId userID, Title title, Description description, StatusAlert status) {
        this.id = id;
        this.petId = petId;
        this.userID = userID;
        this.title = title;
        this.description = description;
        this.statusAlert = status;
    }

    public AlertEntity toEntity() {
        return new AlertEntity(
                this.id.toString(),
                this.petId.toString(),
                this.userID.value(),
                this.title.getValue(),
                this.description.getValue(),
                this.statusAlert.getStatusName()
        );
    }

    public StatusAlert currentStatus() {
        return statusAlert;
    }

    public void setStatus(StatusAlert statusAlert) {
        this.statusAlert = statusAlert;
    }

    public void open() {
        statusAlert.open(this);
    }

    public void seen() {
        statusAlert.seen(this);
    }

    public void safe() {
        statusAlert.safe(this);
    }

    public void closed() {
        statusAlert.closed(this);
    }

    public void updateTitle(Title title){
        this.title = title;
    }

    public void updateDescription(Description description){
        this.description = description;
    }

}
