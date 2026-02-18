package itacademy.pawalert.domain.user.model;

import java.util.regex.Pattern;

public record PhoneNumber(String value) {

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^\\+?[0-9\\-\\s()]{7,20}$");

    public PhoneNumber {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Phone number cannot be blank");
        }

        if (!PHONE_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("Invalid phone number format");
        }

        if (value.length() < 7 || value.length() > 20) {
            throw new IllegalArgumentException("Phone number must have between 7 and 20 characters");
        }
    }

    public static PhoneNumber of(String phoneNumber) {
        return new PhoneNumber(phoneNumber);
    }

    public boolean isMobile() {
        String national = getNationalNumber();
        if (national.isEmpty()) return false;
        char first = national.charAt(0);
        return first == '6' || first == '7';
    }

    public String getCountryCode() {
        if (value.startsWith("+")) {
            int end = Math.min(4, value.length());
            return value.substring(0, end);
        }
        return "";
    }

    public String getNationalNumber() {
        if (value.startsWith("+")) {
            return value.substring(getCountryCode().length());
        }
        return value;

    }
}