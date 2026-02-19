package itacademy.pawalert.infrastructure.rest.metadata.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MetadataEnumListDto {

    @JsonSetter(nulls = Nulls.SKIP)
    private String type;

    @JsonSetter(nulls = Nulls.SKIP)
    private List<MetadataDto> metadata;
}