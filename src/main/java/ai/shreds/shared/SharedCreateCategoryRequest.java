package ai.shreds.shared;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.validator.constraints.UniqueElements;
import javax.validation.Valid;

import java.util.UUID;
import java.util.List;
import java.util.Map;

/**
 * Request DTO for creating a new category.
 * Enforces field validation constraints to ensure data integrity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SharedCreateCategoryRequest {

    /**
     * Name of the category.
     */
    @NotNull(message = "The 'name' field is required")
    @Size(max = 255, message = "The 'name' field must be at most 255 characters")
    private String name;

    /**
     * Description of the category.
     */
    @Size(max = 1000, message = "The 'description' field must be at most 1000 characters")
    private String description;

    /**
     * ID of the parent category if it exists.
     */
    private UUID parentCategoryId;

    /**
     * List of tags associated with the category.
     */
    @UniqueElements(message = "Tags should be unique within the category to prevent duplicate tag entries")
    @Size(max = 10, message = "A maximum of 10 tags are allowed")
    private List<
        @Size(max = 50, message = "Each tag must be at most 50 characters")
        String> tags;

    /**
     * Additional metadata for the category.
     */
    @Valid
    private Map<String, Object> metadata;

}