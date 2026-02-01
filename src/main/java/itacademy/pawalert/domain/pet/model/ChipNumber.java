package itacademy.pawalert.domain.pet.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record ChipNumber(@NotNull(message = "Chip can not be empty")
                         @Size(min = 15, max = 15, message = "The chip must be exactly 15 characters long")
                         @Pattern(regexp="^[0-9]*$", message = "The chip must be only made up of numeric characters")
                         @ValidCountryChipCode(message = "Invalid country chip code") String chip) {

}
