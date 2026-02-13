package itacademy.pawalert.domain.image.model;

import lombok.Getter;

@Getter
public enum ContentSafetyStatus implements ImageDisplayableEnum {
    SAFE("Safe"), QUESTIONABLE("Questionable"), UNSAFE("Unsafe");

    private final String value;

    ContentSafetyStatus(String value) {
        this.value = value;
    }

    @Override
    public String getDisplayName() {
        return value;
    }
}
