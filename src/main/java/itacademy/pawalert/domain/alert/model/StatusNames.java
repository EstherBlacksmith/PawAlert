package itacademy.pawalert.domain.alert.model;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

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

    public static List<String> getActiveStatusNames() {
        return Arrays.stream(values())
                .filter(status -> status != CLOSED)
                .map(StatusNames::name)
                .toList();
    }
}
