package itacademy.pawalert.domain;

import lombok.Getter;
import lombok.Setter;

public class ClosedStatusAlert implements StatusAlert {
    @Getter
    @Setter
    ClosureReason closureReason;

    @Override
    public void open(Alert alert) {
        System.out.println("The alert is already closed");
    }

    @Override
    public void seen(Alert alert) {
        System.out.println("Thea alert is already closed");
    }

    @Override
    public void closed(Alert alert) {
        System.out.println("Thea alert is already closed");
    }

    @Override
    public void safe(Alert alert) {
        System.out.println("Thea alert is already closed");
    }

    @Override
    public StatusNames getStatusName() {
        return StatusNames.CLOSED;
    }
}
