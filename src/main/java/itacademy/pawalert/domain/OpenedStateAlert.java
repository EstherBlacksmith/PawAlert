package itacademy.pawalert.domain;

public class OpenedStateAlert implements StatusAlert {
    @Override
    public void open(Alert alert) {
        System.out.println("Thea alert is already opened");
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
    public StatusNames getStateName() {
        return StatusNames.OPENED;
    }
}
