package itacademy.pawalert.domain.alert.model;

public interface StatusAlert {
    Alert open(Alert alert);

    Alert seen(Alert alert);

    Alert safe(Alert alert);

    Alert closed(Alert alert);

    StatusNames getStatusName();

}
