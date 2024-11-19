package ai.shreds.application.ports;

import ai.shreds.shared.SharedCategoryResponse;
import ai.shreds.shared.SharedCreateCategoryRequest;
import ai.shreds.shared.SharedUpdateCategoryRequest;
import ai.shreds.shared.SharedCategoryFilterCriteria;

import java.util.List;
import java.util.UUID;

public interface ApplicationCategoryInputPort {
    
    /**
     * Creates a new category based on the provided request data.
     * @param request the data for creating a new category
     * @return the created category response
     */
    SharedCategoryResponse createCategory(SharedCreateCategoryRequest request);

    /**
     * Retrieves a list of categories based on the specified filter criteria.
     * @param filter the criteria for filtering categories
     * @return a list of category responses
     */
    List<SharedCategoryResponse> getCategories(SharedCategoryFilterCriteria filter);

    /**
     * Retrieves a specific category by its unique identifier.
     * @param categoryId the unique identifier of the category
     * @return the category response
     */
    SharedCategoryResponse getCategoryById(UUID categoryId);

    /**
     * Updates an existing category with the new data provided in the request.
     * @param categoryId the unique identifier of the category to update
     * @param request the new data for updating the category
     * @return the updated category response
     */
    SharedCategoryResponse updateCategory(UUID categoryId, SharedUpdateCategoryRequest request);

    /**
     * Deletes a category identified by its unique identifier, with optional cascading to subcategories.
     * @param categoryId the unique identifier of the category to delete
     * @param cascade whether to cascade the deletion to subcategories
     */
    void deleteCategory(UUID categoryId, boolean cascade);
}
