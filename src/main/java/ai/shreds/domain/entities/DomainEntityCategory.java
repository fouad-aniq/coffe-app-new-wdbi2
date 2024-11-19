package ai.shreds.domain.entities;

import java.util.*;
import java.sql.Timestamp;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.*;

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

    /**
     * Adds a tag to the category if it's not already present.
     *
     * @param tag the tag to add
     */
    public void addTag(String tag) {
        if (!tags.contains(tag)) {
            tags.add(tag);
        }
    }

    /**
     * Sets the parent category while preventing self-parenting and cyclical hierarchies,
     * and ensuring the category's name is unique under the same parent.
     *
     * @param parentCategory the parent category to set
     * @throws IllegalArgumentException if parentCategory is invalid
     */
    public void setParentCategory(DomainEntityCategory parentCategory) {
        if (parentCategory != null) {
            if (parentCategory.equals(this)) {
                throw new IllegalArgumentException("A category cannot be its own parent.");
            }
            if (isDescendantOf(parentCategory)) {
                throw new IllegalArgumentException("Cannot set parentCategory as it would create a cyclical hierarchy.");
            }
            if (parentCategory.getHierarchyDepth() >= 10) {
                throw new IllegalArgumentException("Hierarchy depth exceeds the maximum allowed depth of 10.");
            }
            // Ensure name uniqueness under the same parent
            if (parentCategory.hasSubcategoryWithName(this.name)) {
                throw new IllegalArgumentException("A category with the same name already exists under the parent category.");
            }
        }
        // Remove from current parent
        if (this.parentCategory != null) {
            this.parentCategory.subcategories.remove(this);
        }
        this.parentCategory = parentCategory;
        // Add to new parent's subcategories if not null
        if (parentCategory != null) {
            parentCategory.subcategories.add(this);
        }
    }

    /**
     * Adds a subcategory to this category while checking the hierarchy depth limit
     * and ensuring the subcategory's name is unique among existing subcategories.
     *
     * @param subcategory the subcategory to add
     * @throws IllegalArgumentException if adding the subcategory violates hierarchy rules
     */
    public void addSubcategory(DomainEntityCategory subcategory) {
        if (subcategory == null) {
            throw new IllegalArgumentException("Subcategory cannot be null.");
        }
        if (subcategory.equals(this)) {
            throw new IllegalArgumentException("A category cannot be a subcategory of itself.");
        }
        if (this.isAncestorOf(subcategory)) {
            throw new IllegalArgumentException("Cannot add subcategory as it would create a cyclical hierarchy.");
        }
        if (this.getHierarchyDepth() >= 10) {
            throw new IllegalArgumentException("Hierarchy depth exceeds the maximum allowed depth of 10.");
        }
        if (hasSubcategoryWithName(subcategory.getName())) {
            throw new IllegalArgumentException("A subcategory with the same name already exists under this category.");
        }
        subcategory.setParentCategory(this);
    }

    /**
     * Checks if this category has a subcategory with the given name.
     *
     * @param name the name to check
     * @return true if a subcategory with the name exists, false otherwise
     */
    public boolean hasSubcategoryWithName(String name) {
        for (DomainEntityCategory subcategory : subcategories) {
            if (subcategory.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the given category is a descendant of this category.
     *
     * @param category the category to check
     * @return true if the given category is a descendant, false otherwise
     */
    public boolean isAncestorOf(DomainEntityCategory category) {
        DomainEntityCategory current = category;
        while (current != null) {
            if (current.equals(this)) {
                return true;
            }
            current = current.getParentCategory();
        }
        return false;
    }

    /**
     * Checks if this category is a descendant of the given category.
     *
     * @param category the category to check
     * @return true if this category is a descendant, false otherwise
     */
    public boolean isDescendantOf(DomainEntityCategory category) {
        DomainEntityCategory current = this;
        while (current != null) {
            if (current.equals(category)) {
                return true;
            }
            current = current.getParentCategory();
        }
        return false;
    }

    /**
     * Calculates the depth of this category in the hierarchy.
     *
     * @return the hierarchy depth
     */
    public int getHierarchyDepth() {
        int depth = 0;
        DomainEntityCategory current = this;
        while (current.getParentCategory() != null) {
            depth++;
            current = current.getParentCategory();
        }
        return depth;
    }

    /**
     * Validates the category according to business rules.
     *
     * @throws IllegalArgumentException if validations fail
     */
    public void validate() {
        if (name == null || name.length() > 255) {
            throw new IllegalArgumentException("The 'name' field must not be null and have a maximum length of 255 characters.");
        }
        if (tags.size() != new HashSet<>(tags).size()) {
            throw new IllegalArgumentException("Tags associated with a category should be unique within that category.");
        }
        if (getHierarchyDepth() > 10) {
            throw new IllegalArgumentException("The depth of the category hierarchy exceeds the maximum allowed depth of 10.");
        }
        if (metadata != null) {
            for (Map.Entry<String, Object> entry : metadata.entrySet()) {
                if (entry.getKey() == null || entry.getKey().trim().isEmpty()) {
                    throw new IllegalArgumentException("Metadata keys must not be null or empty.");
                }
                if (entry.getValue() == null) {
                    throw new IllegalArgumentException("Metadata values must not be null.");
                }
                // Additional checks for disallowed keys or values can be added here
            }
        }
        // Ensure that subcategories have unique names under the same parent category
        Set<String> subcategoryNames = new HashSet<>();
        for (DomainEntityCategory subcategory : subcategories) {
            if (!subcategoryNames.add(subcategory.getName())) {
                throw new IllegalArgumentException("Subcategories must have unique names under the same parent category.");
            }
        }
    }

    /**
     * Deletes the category; if cascade is true, all its subcategories must also be deleted.
     *
     * @param cascade indicates whether to delete subcategories
     */
    public void deleteCategory(boolean cascade) {
        if (cascade) {
            for (DomainEntityCategory subcategory : new ArrayList<>(subcategories)) {
                subcategory.deleteCategory(true);
            }
            subcategories.clear();
        }
        // Remove this category from its parent's subcategories
        if (parentCategory != null) {
            parentCategory.subcategories.remove(this);
        }
        // Logic to delete this category from repository would go here
    }

}
