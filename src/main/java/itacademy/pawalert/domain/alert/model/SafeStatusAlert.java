package itacademy.pawalert.domain.alert.model;

import itacademy.pawalert.domain.alert.exception.InvalidAlertStatusChange;
import itacademy.pawalert.domain.alert.service.AlertFactory;

public class SafeStatusAlert implements StatusAlert {
    @Override
    public Alert open(Alert alert) {
        throw InvalidAlertStatusChange.invalidTransition("SAFE", "OPENED");
    }

    @Override
    public Alert seen(Alert alert) {
        throw InvalidAlertStatusChange.invalidTransition("SAFE", "SEEN");
    }

    @Override
    public Alert closed(Alert alert) {
        return AlertFactory.markAsClose(alert);
    }

    @Override
    public Alert safe(Alert alert) {
        throw InvalidAlertStatusChange.invalidTransition("SAFE", "SAFE");
    }

    @Override
    public StatusNames getStatusName() {
        return StatusNames.SAFE;
    }
}
