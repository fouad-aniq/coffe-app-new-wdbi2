package ai.shreds.shared;

import lombok.experimental.UtilityClass;

import java.util.UUID;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;

@UtilityClass
public class ValidCategoryParent {

    /**
     * Validates that a category is not its own parent and is not a descendant of itself.
     *
     * @param categoryId The ID of the category being validated.
     * @param parentCategoryId The ID of the potential parent category.
     * @param categoryMap A map of category IDs to SharedCategoryDTOs.
     * @throws IllegalArgumentException if the validation fails.
     */
    public void validateCategoryParent(UUID categoryId, UUID parentCategoryId, Map<UUID, SharedCategoryDTO> categoryMap) {
        if (parentCategoryId == null) {
            return; // Valid, root category
        }
        if (parentCategoryId.equals(categoryId)) {
            throw new IllegalArgumentException(\"A category cannot be its own parent.\");
        }

        Set<UUID> visitedCategories = new HashSet<>();
        UUID currentParentId = parentCategoryId;

        while (currentParentId != null) {
            if (currentParentId.equals(categoryId)) {
                throw new IllegalArgumentException(\"A category cannot be a descendant of itself.\");
            }
            if (!visitedCategories.add(currentParentId)) {
                throw new IllegalArgumentException(\"Cyclical hierarchy detected.\");
            }
            SharedCategoryDTO parentCategory = categoryMap.get(currentParentId);
            if (parentCategory != null) {
                currentParentId = parentCategory.getParentCategoryId();
            } else {
                currentParentId = null; // No further parent, end of hierarchy
            }
        }
    }
}