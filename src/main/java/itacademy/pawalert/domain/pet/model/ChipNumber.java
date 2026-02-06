package itacademy.pawalert.domain.pet.model;


public record ChipNumber(@ValidCountryChipCode(message = "Invalid country chip code") String value) {

    private static final int MIN_LENGTH = 9;
    private static final int MAX_LENGTH = 15;
    public ChipNumber {

        value = value.trim();

        if (!value.matches("^[0-9]*$")) {
            throw new IllegalArgumentException("The chip must be only made up of numeric characters");
        }

        if (value.length() < MIN_LENGTH || value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "The chip must be exactly 15 characters long");
        }
    }

    public static ChipNumber empty() {
        return new ChipNumber(null);
    }

    public boolean isPresent() {
        return value != null;
    }

    public String value() {
        return this.value; }

    public static ChipNumber of(String value) {
        return new ChipNumber(value);
    }
}
