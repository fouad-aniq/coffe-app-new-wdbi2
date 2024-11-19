package ai.shreds.infrastructure.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.sql.Timestamp;
import java.util.*;

/**
 * Represents a category that can be hierarchically structured and associated with multiple tags for flexible classification.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "categories", uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "parent_category_id"})})
public class InfrastructureCategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @NotNull
    @Column(nullable = false)
    private UUID id;

    @NotNull
    @Size(max = 255)
    @Column(nullable = false, length = 255)
    private String name;

    @Size(max = 500)
    @Column(length = 500)
    private String description;

    /**
     * Self-referential association to represent the parent category.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_category_id")
    private InfrastructureCategoryEntity parentCategory;

    /**
     * Self-referential association to represent child categories.
     */
    @OneToMany(mappedBy = "parentCategory", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InfrastructureCategoryEntity> subcategories = new ArrayList<>();

    /**
     * Tags associated with the category for flexible classification.
     * Tags are unique within the category to prevent duplicates.
     */
    @ElementCollection
    @CollectionTable(name = "category_tags", joinColumns = @JoinColumn(name = "category_id"))
    @Column(name = "tag")
    private Set<String> tags = new HashSet<>();

    /**
     * Metadata in JSON format, validated to ensure it conforms to expected structure.
     */
    @Column(columnDefinition = "jsonb")
    @Type(type = "jsonb")
    private Map<String, Object> metadata;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @PrePersist
    @PreUpdate
    private void validate() {
        // Prevent cyclical hierarchies
        if (isCyclic(this, new HashSet<>())) {
            throw new IllegalStateException("A category cannot be its own parent or create a cycle in the hierarchy.");
        }

        // Validate hierarchy depth
        if (getHierarchyDepth() > 10) {
            throw new IllegalStateException("The depth of the category hierarchy cannot exceed 10.");
        }

        // Validate metadata
        validateMetadata(metadata);
    }

    private boolean isCyclic(InfrastructureCategoryEntity category, Set<UUID> visited) {
        if (category.parentCategory != null) {
            if (visited.contains(category.parentCategory.getId())) {
                return true;
            } else {
                visited.add(category.parentCategory.getId());
                return isCyclic(category.parentCategory, visited);
            }
        }
        return false;
    }

    private int getHierarchyDepth() {
        int depth = 1;
        InfrastructureCategoryEntity current = this.parentCategory;
        while (current != null) {
            depth++;
            current = current.parentCategory;
        }
        return depth;
    }

    private void validateMetadata(Map<String, Object> metadata) {
        // Implement specific validation rules for metadata
        if (metadata != null) {
            // Example validation: disallow certain keys
            Set<String> disallowedKeys = new HashSet<>(Arrays.asList("forbiddenKey1", "forbiddenKey2"));
            for (String key : metadata.keySet()) {
                if (disallowedKeys.contains(key)) {
                    throw new IllegalStateException("Metadata contains disallowed key: " + key);
                }
            }
        }
    }
}