package ai.shreds.shared;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.UUID;

// Add Jackson annotations for proper JSON serialization
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Data Transfer Object for Category.
 * Acts as a DTO to transfer category data between layers,
 * without exposing internal domain models directly.
 */
@Data // Replaces @Getter, @Setter, @ToString, @EqualsAndHashCode
@NoArgsConstructor // Generates no-argument constructor required for deserialization
@AllArgsConstructor // Generates all-arguments constructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SharedCategoryDTO {

    // Fields as per UML diagram
    private UUID id;
    private String name;
    private String description;
    private UUID parentCategoryId;
    private List<String> tags;
    private Map<String, Object> metadata;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}