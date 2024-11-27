package ai.shreds.shared;

import java.util.UUID;
import java.util.List;
import java.util.Map;
import java.sql.Timestamp;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Data Transfer Object (DTO) used in API responses to transfer category data between layers,
 * particularly between the presentation layer (controllers) and the application/core layers,
 * without exposing internal domain models directly.
 * It represents a category, including its hierarchical relationships and subcategories,
 * allowing clients to understand the full category structure.
 *
 * Note: To prevent performance issues, the depth of the category hierarchy represented by
 * subcategories should be limited to a maximum depth of 10. This limit should be enforced
 * elsewhere in the application logic.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SharedCategoryResponse {
    private UUID id;
    private String name;
    private String description;
    private UUID parentCategoryId;
    private List<String> tags;
    private Map<String, Object> metadata;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private List<SharedCategoryResponse> subcategories;
}