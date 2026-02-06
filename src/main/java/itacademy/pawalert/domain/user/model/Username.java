package itacademy.pawalert.domain.user.model;


public record Username(String value) {

    public Username {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Username cannot be blank");
        }
        if (value.length() < 3 || value.length() > 50) {
            throw new IllegalArgumentException("Username must be 3-50 characters");
        }
    }

    public static Username of(String username) {
        return new Username(username);
    }
}