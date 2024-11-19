package ai.shreds.shared;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.AssertTrue;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.hibernate.validator.constraints.UniqueElements;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SharedCreateCategoryRequest {

    @NotNull(message = "Name cannot be null")
    @NotEmpty(message = "Name cannot be empty")
    private String name;

    private String description;

    private UUID parentCategoryId;

    @UniqueElements(message = "Tags must be unique within the category")
    private List<@NotEmpty(message = "Tag cannot be empty") String> tags;

    private Map<String, Object> metadata;

    @AssertTrue(message = "Category cannot be its own parent or a descendant of itself")
    private boolean isParentCategoryValid() {
        // Logic to prevent cyclical hierarchies
        // Placeholder for actual implementation
        return true;
    }

    @AssertTrue(message = "Metadata contains invalid keys or structure")
    private boolean isMetadataValid() {
        // Logic to validate metadata structure and disallowed keys or values
        // Placeholder for actual implementation
        return true;
    }
}