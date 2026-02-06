package itacademy.pawalert.domain.user.model;

import java.util.regex.Pattern;


public record Email(String value) {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    public Email {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Email can't be empty");
        }


        value = value.trim().toLowerCase();
        if (!EMAIL_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("Invalid email: " + value);
        }
    }

    public static Email of(String email) {
        return new Email(email);
    }


    //For Optional
    public static Email ofNullable(String email) {
        if (email == null || email.isBlank()) {
            return null;
        }
        try {
            return new Email(email);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }


}
