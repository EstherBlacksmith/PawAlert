package itacademy.pawalert.domain.pet.model;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Set;

public class CountryChipCodeValidator implements ConstraintValidator<ValidCountryChipCode, String> {
    private static final Set<String> VALID_COUNTRY_CODES = Set.of(
            "076", "826", "840", "724" /*Spain*/, "380", "250", "276", "528", "578", "752",
            "036", "124", "356", "410", "156", "643", "032", "068", "152", "170",
            "218", "600", "604", "858", "862"
    );

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true;
        }

        if (value.length() < 3) {
            return false;
        }

        String countryCode = value.substring(0, 3);
        return VALID_COUNTRY_CODES.contains(countryCode);
    }
}
