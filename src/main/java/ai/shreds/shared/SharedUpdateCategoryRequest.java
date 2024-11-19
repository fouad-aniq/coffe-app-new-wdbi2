package ai.shreds.shared;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.Valid;
import org.hibernate.validator.constraints.UniqueElements;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SharedUpdateCategoryRequest {

    private String name;

    private String description;

    private UUID parentCategoryId;

    @Valid
    @UniqueElements(message = "Tags must be unique")
    private List<@NotBlank(message = "Tag must not be blank") String> tags;

    @Valid
    private Map<String, Object> metadata;
}