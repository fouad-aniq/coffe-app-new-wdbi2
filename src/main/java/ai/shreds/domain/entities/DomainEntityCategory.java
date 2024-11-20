package ai.shreds.domain.entities;

import java.util.UUID;
import java.util.List;
import java.util.Map;
import java.sql.Timestamp;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"parentCategory", "subcategories"})
@ToString(exclude = {"parentCategory", "subcategories"})
public class DomainEntityCategory {
    private UUID id;
    private String name;
    private String description;
    private DomainEntityCategory parentCategory;
    private List<String> tags;
    private Map<String, Object> metadata;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private List<DomainEntityCategory> subcategories;

    // Method to set the ID of the category
    public void setId(UUID id) {
        this.id = id;
    }
}