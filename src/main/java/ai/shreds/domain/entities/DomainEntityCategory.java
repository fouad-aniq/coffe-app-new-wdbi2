package ai.shreds.domain.entities;

import lombok.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.*;
import java.sql.Timestamp;

/**
 * Represents a category that can be hierarchically structured and associated with multiple tags for flexible classification.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DomainEntityCategory {

    private UUID id;

    @NotNull
    @Size(max = 255)
    private String name;

    private String description;

    private DomainEntityCategory parentCategory;

    @Singular("tag")
    private List<String> tags = new ArrayList<>();

    private Map<String, Object> metadata = new HashMap<>();

    private Timestamp createdAt;

    private Timestamp updatedAt;

    @Singular("subcategory")
    private List<DomainEntityCategory> subcategories = new ArrayList<>();
}