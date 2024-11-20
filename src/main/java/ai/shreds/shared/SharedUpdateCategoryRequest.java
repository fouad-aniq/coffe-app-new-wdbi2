package ai.shreds.shared;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.UniqueElements;
import java.util.List;
import java.util.Map;

/**
 * Request object for updating a Category.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SharedUpdateCategoryRequest {

    /**
     * The name of the category.
     */
    private String name;

    /**
     * The description of the category.
     */
    private String description;

    /**
     * A list of tags associated with the category.
     * Each tag must not be blank and tags must be unique.
     */
    @UniqueElements(message = "Tags must be unique")
    private List<@NotBlank(message = "Tag must not be blank") String> tags;

    /**
     * Additional metadata for the category.
     */
    private Map<String, Object> metadata;
}