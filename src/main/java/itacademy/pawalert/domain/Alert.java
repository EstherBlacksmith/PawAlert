package itacademy.pawalert.domain;

import java.util.List;
import java.util.UUID;

public class Alert {

    private UUID id;
    private final Tittle tittle;
    private final Description description;
    private StatusAlert statusAlert;
    private ClosureReason closureReason;
    private  List<AlertEvent> history;

    public Alert(Tittle tittle, Description description) {
        this.tittle = tittle;
        this.description = description;
        this.statusAlert = new OpenedStateAlert();

    }

    public AlertEvent getPreviousState() {
        return history.getLast();
    }

    public AlertEvent getNextState(){
        return history.getLast();
    }

    public StatusAlert currentState(){
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
