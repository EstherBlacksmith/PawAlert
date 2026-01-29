package itacademy.pawalert.domain;

public class ClosedStatusAlert implements StatusAlert {


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
    public StatusNames getStateName() {
        return StatusNames.CLOSED;
    }
}
