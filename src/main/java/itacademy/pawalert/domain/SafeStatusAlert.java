package itacademy.pawalert.domain;

public class SafeStatusAlert implements StatusAlert {
    @Override
    public void open(Alert alert) {
        System.out.println("The alert is already opened");
    }

    @Override
    public void seen(Alert alert) {
        System.out.println("The alert is safe");
    }

    @Override
    public void closed(Alert alert) {
        alert.setStatus(new ClosedStatusAlert());
    }

    @Override
    public void safe(Alert alert) {
        System.out.println("The alert is safe");
    }

    @Override
    public StatusNames getStatusName() {
        return StatusNames.SAFE;
    }
}
