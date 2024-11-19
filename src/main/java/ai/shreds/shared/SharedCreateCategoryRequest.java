package ai.shreds.shared;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.hibernate.validator.constraints.UniqueElements;
import ai.shreds.shared.validation.ValidCategoryParent;
import ai.shreds.shared.validation.ValidMetadata;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SharedCreateCategoryRequest {

    @NotNull(message = "Name cannot be null")
    @NotEmpty(message = "Name cannot be empty")
    private String name;

    private String description;

    @ValidCategoryParent
    private UUID parentCategoryId;

    @UniqueElements(message = "Tags must be unique within the category")
    private List<@NotEmpty(message = "Tag cannot be empty") String> tags;

    @ValidMetadata
    private Map<String, Object> metadata;
}