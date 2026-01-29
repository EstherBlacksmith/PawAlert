package itacademy.pawalert.domain;

public interface StatusAlert {
    void open(Alert alert);
    void seen(Alert alert);
    void safe(Alert alert);
    void closed(Alert alert);
    StatusNames getStateName();
}
