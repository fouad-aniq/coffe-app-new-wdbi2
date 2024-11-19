package ai.shreds.shared;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Data Transfer Object for updating a category.
 * All fields are optional to allow partial updates.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SharedUpdateCategoryRequest {
    private String name;
    private String description;
    private UUID parentCategoryId;
    private List<String> tags;
    private Map<String, Object> metadata;
}
