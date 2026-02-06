package itacademy.pawalert.domain.user.model;

import java.util.regex.Pattern;

public record Password(String value) {

    private static final int MIN_LENGTH = 8;
    private static final int MAX_LENGTH = 128;
    private static final Pattern SPECIAL_CHARS_PATTERN =
            Pattern.compile("[@$!%*?&_#]");
    private static final Pattern COMMON_PASSWORDS = Pattern.compile(
            "(?i)^(password|123456|qwerty|admin|letmein|welcome)$"
    );

    public Password {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Password cannot be blank");
        }
        if (value.length() < MIN_LENGTH) {
            throw new IllegalArgumentException("Password must be at least " + MIN_LENGTH + " characters");
        }

        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "Password cannot exceed " + MAX_LENGTH + " characters");
        }

        if (value.chars().noneMatch(Character::isUpperCase)) {
            throw new IllegalArgumentException("Password must contain at least one uppercase letter");
        }
        if (value.chars().noneMatch(Character::isLowerCase)) {
            throw new IllegalArgumentException("Password must contain at least one lowercase letter");
        }
        if (value.chars().noneMatch(Character::isDigit)) {
            throw new IllegalArgumentException("Password must contain at least one number");
        }

        if (!SPECIAL_CHARS_PATTERN.matcher(value).find()) {
            throw new IllegalArgumentException("Password must contain at least one special character (@$!%*?&)");
        }
    }

    public static Password fromPlainText(String plainPassword) {
        return new Password(plainPassword);
    }
}
