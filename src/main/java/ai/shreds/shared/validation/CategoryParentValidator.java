package ai.shreds.shared;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import lombok.experimental.UtilityClass;
import ai.shreds.shared.SharedCategoryResponse;

/**
 * Utility class for validating parent categories to prevent circular references and invalid parent assignments.
 */
@UtilityClass
public class CategoryParentValidator {

    /**
     * Validates that the new parent category ID is valid for the given category.
     *
     * @param category             The category being updated.
     * @param newParentCategoryId  The ID of the proposed new parent category.
     * @throws IllegalArgumentException if the new parent category ID is invalid.
     */
    public void validateParentCategory(SharedCategoryResponse category, UUID newParentCategoryId) {
        if (newParentCategoryId == null) {
            // No parent to validate
            return;
        }
        if (newParentCategoryId.equals(category.getId())) {
            throw new IllegalArgumentException(\"A category cannot be a descendant of itself.\");
        }
        Set<UUID> descendantIds = getDescendantCategoryIds(category);
        if (descendantIds.contains(newParentCategoryId)) {
            throw new IllegalArgumentException(\"Cyclical hierarchy detected.\");
        }
    }

    /**
     * Recursively collects all descendant category IDs of the given category.
     *
     * @param category The category whose descendants are to be collected.
     * @return A set of UUIDs representing the IDs of all descendant categories.
     */
    private Set<UUID> getDescendantCategoryIds(SharedCategoryResponse category) {
        Set<UUID> descendantIds = new HashSet<>();
        List<SharedCategoryResponse> subcategories = category.getSubcategories();
        if (subcategories != null) {
            for (SharedCategoryResponse subcategory : subcategories) {
                descendantIds.add(subcategory.getId());
                descendantIds.addAll(getDescendantCategoryIds(subcategory));
            }
        }
        return descendantIds;
    }
}