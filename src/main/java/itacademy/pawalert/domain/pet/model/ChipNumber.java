package itacademy.pawalert.domain.pet.model;

import java.util.Set;

public record ChipNumber(String value) {

    private static final int MIN_LENGTH = 9;
    private static final int MAX_LENGTH = 15;

    private static final Set<String> VALID_COUNTRY_CODES = Set.of(
            "076", "826", "840", "724", "380", "250", "276", "528", "578", "752",
            "036", "124", "356", "410", "156", "643", "032", "068", "152", "170",
            "218", "600", "604", "858", "862"
    );

    private ChipNumber(String value, boolean isValid) {
        this(value);
    }

    public static ChipNumber empty() {
        return new ChipNumber(null, true);
    }

    public static ChipNumber of(String value) {
        validate(value);
        return new ChipNumber(value.trim(), true);
    }

    public static ChipNumber ofNullable(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        validate(value);
        return new ChipNumber(value.trim(), true);
    }

    private static void validate(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Chip number cannot be empty");
        }

        String trimmed = value.trim();

        if (!trimmed.matches("^[0-9]*$")) {
            throw new IllegalArgumentException("The chip must be only numeric characters");
        }

        if (trimmed.length() < MIN_LENGTH || trimmed.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Chip must be between " + MIN_LENGTH + " and " + MAX_LENGTH + " characters");
        }

        if (trimmed.length() >= 3) {
            String countryCode = trimmed.substring(0, 3);
            if (!VALID_COUNTRY_CODES.contains(countryCode)) {
                throw new IllegalArgumentException("Invalid country code: " + countryCode);
            }
        }
    }

    public boolean isPresent() {
        return value != null && !value.isBlank();
    }

    public String value() {
        return this.value;
    }
}
