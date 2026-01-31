package itacademy.pawalert.domain;

import itacademy.pawalert.infrastructure.persistence.AlertEntity;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

public class Alert {

    private final UUID id;
    @Getter
    private final UUID petId;
    private final Tittle tittle;
    private final Description description;
    private StatusAlert statusAlert;
    private ClosureReason closureReason;
    private List<AlertEvent> history;

    public Alert(UUID petId, Tittle tittle, Description description) {
        this.id = UUID.randomUUID();
        this.petId = petId;
        this.tittle = tittle;
        this.description = description;
        this.statusAlert = new OpenedStateAlert();

    }

    public Alert(UUID id, UUID petId, Tittle tittle, Description description) {
        this.id = id;
        this.petId = petId;
        this.tittle = tittle;
        this.description = description;
        this.statusAlert = new OpenedStateAlert();
    }

    public AlertEntity toEntity() {
        return new AlertEntity(
                this.id.toString(),
                this.petId.toString(),
                this.tittle.getValue(),
                this.description.getValue(),
                this.statusAlert.getStatusName()
        );
    }

 /* public AlertEvent getPreviousState() {
        return history.getLast();
    }

    public AlertEvent getNextState(){
        return history.getLast();
    }
*/  public StatusAlert currentStatus(){
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

   }
