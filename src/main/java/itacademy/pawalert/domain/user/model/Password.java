package itacademy.pawalert.domain.user.model;


// Password.java - with compact constructor validation
public record Password(String value) {

    public Password {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Password cannot be blank");
        }
        if (value.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters");
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
        if (!value.matches(".*[@$!%*?&].*")) {
            throw new IllegalArgumentException("Password must contain at least one special character (@$!%*?&)");
        }
    }

    public static Password fromPlainText(String plainPassword) {
        return new Password(plainPassword);
    }
}
