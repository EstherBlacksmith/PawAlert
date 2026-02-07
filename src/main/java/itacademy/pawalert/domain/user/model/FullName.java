package itacademy.pawalert.domain.user.model;

public record FullName(Username  username, Surname lastName) {
    public FullName {
        if (username == null) {
            throw new IllegalArgumentException("Name can't be empty");
        }
        if (lastName == null ) {
            throw new IllegalArgumentException("Surname can't be empty");
        }

    }

    public String getFullName() {
        return username + " " + lastName;
    }

    // ============ FACTORY METHODS ============

    public static FullName of(Username firstName, Surname lastName) {
        return new FullName(firstName, lastName);
    }

    public static FullName of(String firstName, String lastName) {
        return new FullName(
                Username.of(firstName),
                Surname.of(lastName)
        );
    }
}