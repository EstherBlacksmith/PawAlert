package itacademy.pawalert.domain.alert.model;

import itacademy.pawalert.domain.alert.exception.InvalidAlertStatusChange;

public class SafeStatusAlert implements StatusAlert {
    @Override
    public void open(Alert alert) {
        throw InvalidAlertStatusChange.invalidTransition("SAFE", "OPENED");
    }

    @Override
    public void seen(Alert alert) {
        throw InvalidAlertStatusChange.invalidTransition("SAFE", "SEEN");
    }

    @Override
    public void closed(Alert alert) {
        alert.setStatus(new ClosedStatusAlert());
    }

    @Override
    public void safe(Alert alert) {
        throw InvalidAlertStatusChange.invalidTransition("SAFE", "SAFE");
    }

    @Override
    public StatusNames getStatusName() {
        return StatusNames.SAFE;
    }
}
