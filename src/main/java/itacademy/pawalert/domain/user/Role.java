package itacademy.pawalert.domain.user;

import itacademy.pawalert.domain.user.model.UserDisplayableEnum;
import lombok.Getter;

@Getter
public enum Role implements UserDisplayableEnum {
    USER("User"),
    ADMIN("Admin");

    private final String value;

    Role(String value) {
        this.value = value;
    }

    @Override
    public String getDisplayName() {
        return value;
    }
}