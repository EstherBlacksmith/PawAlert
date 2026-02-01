package itacademy.pawalert.domain.pet.model;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CountryChipCodeValidator.class)
public @interface ValidCountryChipCode {
    String message() default "Invalid country code";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
