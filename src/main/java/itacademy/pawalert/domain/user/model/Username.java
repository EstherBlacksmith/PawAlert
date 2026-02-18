package itacademy.pawalert.domain.user.model;


import com.fasterxml.jackson.annotation.JsonValue;

public record Username(@JsonValue String value) {

    private static final java.util.regex.Pattern USERNAME_PATTERN =
            java.util.regex.Pattern.compile("^[a-zA-ZÁÉÍÓÚÜÑáéíóúüñÇç_'\\-\\s]+$");

    public Username {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Username cannot be blank");
        }
        if (value.length() < 3 || value.length() > 50) {
            throw new IllegalArgumentException("Username must be 3-50 characters");
        }

        if (!USERNAME_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("Username only can contain numbers, letters, _ and -");
        }
    }

    public static Username of(String username) {
        return new Username(username);
    }

}