package ai.shreds.shared;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class CategoryParentValidator {

    public static void validateParentCategory(SharedCategoryResponse category, UUID newParentCategoryId) throws Exception {
        if (newParentCategoryId == null) {
            // No parent to validate
            return;
        }
        if (newParentCategoryId.equals(category.getId())) {
            throw new Exception("A category cannot be its own parent.");
        }
        Set<UUID> descendantIds = getDescendantCategoryIds(category);
        if (descendantIds.contains(newParentCategoryId)) {
            throw new Exception("A category cannot be a descendant of itself.");
        }
    }

    private static Set<UUID> getDescendantCategoryIds(SharedCategoryResponse category) {
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
