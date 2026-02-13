package itacademy.pawalert.infrastructure.rest.metadata.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MetadataDto {

    @JsonSetter(nulls = Nulls.SKIP)
    private String paramKey;

    @JsonSetter(nulls = Nulls.SKIP)
    private String paramValue;
}