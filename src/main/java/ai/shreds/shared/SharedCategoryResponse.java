package ai.shreds.shared;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.sql.Timestamp;

/**
 * Data Transfer Object for Category Response including subcategories.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
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