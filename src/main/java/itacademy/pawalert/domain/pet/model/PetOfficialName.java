package itacademy.pawalert.domain.pet.model;


public record PetOfficialName(String value) {

    public String value() {
        return this.value;
    }

    public static PetWorkingName of(String petName) {
        return new PetWorkingName(petName);
    }

    public static PetWorkingName ofNullable(String petName) {
        if (petName == null || petName.isBlank()) {
            return null;
        }
        return new PetWorkingName(petName);
    }

    private static final java.util.regex.Pattern NAME_PATTERN =
            java.util.regex.Pattern.compile("^[a-zA-ZÁÉÍÓÚÜÑáéíóúüñÇç'\\-\\s]+$");

    private static final int MIN_LENGTH = 3;
    private static final int MAX_LENGTH = 50;

    public PetOfficialName {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("The name cant be empty");
        }

        value = value.trim();

        if (value.length() < MIN_LENGTH) {
            throw new IllegalArgumentException("The name must have al least " + MIN_LENGTH + " characters");
        }
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("The name can't exceed " + MAX_LENGTH + " characters");
        }

        if (!NAME_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("El nombre contiene caracteres inválidos");
        }

    }
}