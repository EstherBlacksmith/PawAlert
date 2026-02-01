package itacademy.pawalert.domain.alert.model;

import itacademy.pawalert.domain.alert.exception.InvalidAlertStatusChange;

public class SeenStatusAlert implements StatusAlert {
    @Override
    public void open(Alert alert) {
        throw InvalidAlertStatusChange.invalidTransition("SEEN", "OPENED");
    }

    @Override
    public void seen(Alert alert) {
        alert.setStatus(new SeenStatusAlert());
    }

    @Override
    public void closed(Alert alert) {
        alert.setStatus(new ClosedStatusAlert());
    }

    @Override
    public void safe(Alert alert) {
        alert.setStatus(new SafeStatusAlert());
    }

    @Override
    public StatusNames getStatusName() {
        return StatusNames.SEEN;
    }
}
