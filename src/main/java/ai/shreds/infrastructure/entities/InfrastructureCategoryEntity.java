package ai.shreds.infrastructure.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Represents a category that can be hierarchically structured and associated with multiple tags for flexible classification.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "categories", uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "parent_category_id"})})
public class InfrastructureCategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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
     * Metadata in JSON format.
     */
    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> metadata = new HashMap<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Validation logic has been moved to appropriate service or validator classes to maintain separation of concerns.
}