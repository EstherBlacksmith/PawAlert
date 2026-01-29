package itacademy.pawalert.domain;

import java.util.List;
import java.util.UUID;

public class Alert {

    private UUID id;
    private Tittle tittle;
    private Description description;
    private StatusAlert statusAlert;
    private ClosureReason closureReason;
    private final List<AlertEvent> history;

    public Alert(Tittle tittle, Description description, StatusAlert statusAlert, List<AlertEvent> history) {
        this.tittle = tittle;
        this.description = description;
        this.statusAlert = statusAlert;
        this.history = history;
    }

    public AlertEvent getPreviousState() {
        return history.getLast();
    }
    public void previousState(){}
    public void nextState(){}
    public void currentState(){}

    public void setStatus(StatusAlert statusAlert) {
    }
}
