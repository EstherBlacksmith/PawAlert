package itacademy.pawalert.domain;

public class SeenStatusAlert implements StatusAlert {
    @Override
    public void open(Alert alert) {
        System.out.println("The alert is already opened");
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
