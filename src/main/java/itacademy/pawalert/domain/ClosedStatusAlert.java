package itacademy.pawalert.domain;

import itacademy.pawalert.domain.exception.InvalidAlertStatusChange;
import lombok.Getter;
import lombok.Setter;

public class ClosedStatusAlert implements StatusAlert {
    @Getter
    @Setter
    ClosureReason closureReason;

    @Override
    public void open(Alert alert) {
        throw InvalidAlertStatusChange.alreadyClosed(alert.getId().toString());
    }

    @Override
    public void seen(Alert alert) {
        throw InvalidAlertStatusChange.alreadyClosed(alert.getId().toString());
    }

    @Override
    public void closed(Alert alert) {
        throw InvalidAlertStatusChange.alreadyClosed(alert.getId().toString());
    }

    @Override
    public void safe(Alert alert) {
        throw InvalidAlertStatusChange.alreadyClosed(alert.getId().toString());
    }

    @Override
    public StatusNames getStatusName() {
        return StatusNames.CLOSED;
    }
}
