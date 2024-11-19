package ai.shreds.shared;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.UniqueElements;
import javax.validation.Valid;

import java.util.UUID;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SharedCreateCategoryRequest {

    @NotNull(message = "The 'name' field is required")
    private String name;

    private String description;

    private UUID parentCategoryId;

    @UniqueElements(message = "Tags should be unique within the category to prevent duplicate tag entries")
    private List<String> tags;

    @Valid
    private Map<String, Object> metadata;

}