package itacademy.pawalert.domain.alert.model;

import itacademy.pawalert.domain.alert.exception.InvalidAlertStatusChange;

public class ClosedStatusAlert implements StatusAlert {

    @Override
    public Alert open(Alert alert) {
        throw InvalidAlertStatusChange.alreadyClosed(alert.getId().toString());
    }

    @Override
    public Alert seen(Alert alert) {
        throw InvalidAlertStatusChange.alreadyClosed(alert.getId().toString());
    }

    @Override
    public Alert closed(Alert alert) {
        throw InvalidAlertStatusChange.alreadyClosed(alert.getId().toString());
    }

    @Override
    public Alert safe(Alert alert) {
        throw InvalidAlertStatusChange.alreadyClosed(alert.getId().toString());
    }

    @Override
    public StatusNames getStatusName() {
        return StatusNames.CLOSED;
    }
}
