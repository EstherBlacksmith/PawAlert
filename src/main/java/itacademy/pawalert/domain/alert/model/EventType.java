package itacademy.pawalert.domain.alert.model;

import lombok.Getter;

@Getter
public enum EventType implements AlertDisplayableEnum{
    STATUS_CHANGED("Status changed"),
    TITLE_CHANGED("Title changed"),
    DESCRIPTION_CHANGED("Description changed");

    private final String value;

    EventType(String value) {
        this.value = value;
    }

    @Override
    public String getDisplayName() {
        return value;
    }
}