package itacademy.pawalert.domain.alert.model;

import itacademy.pawalert.domain.alert.exception.InvalidAlertStatusChange;
import itacademy.pawalert.domain.alert.service.AlertFactory;

public class SeenStatusAlert implements StatusAlert {
    @Override
    public Alert open(Alert alert) {
        throw InvalidAlertStatusChange.invalidTransition("SEEN", "OPENED");
    }

    @Override
    public Alert seen(Alert alert) {
        return AlertFactory.markAsSeen(alert);
    }

    @Override
    public Alert closed(Alert alert) {
        return AlertFactory.markAsClosed(alert);
    }

    @Override
    public Alert safe(Alert alert) {
        return AlertFactory.markAsSafe(alert);
    }

    @Override
    public StatusNames getStatusName() {
        return StatusNames.SEEN;
    }
}
