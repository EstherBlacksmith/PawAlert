package itacademy.pawalert.domain.alert.model;

import lombok.Getter;

@Getter
public enum StatusNames implements AlertDisplayableEnum {
    OPENED("Opened"), CLOSED("Closed"), SEEN("Seen"), SAFE("Safe");

    private final String value;

    StatusNames(String value) {
        this.value = value;
    }

    @Override
    public String getDisplayName() {
        return value;
    }
}
