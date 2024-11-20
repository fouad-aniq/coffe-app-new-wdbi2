package ai.shreds.application.ports;

import ai.shreds.shared.SharedCategoryResponse;
import ai.shreds.shared.SharedCreateCategoryRequest;
import ai.shreds.shared.SharedUpdateCategoryRequest;
import ai.shreds.shared.SharedCategoryFilterCriteria;
import ai.shreds.application.exceptions.ApplicationExceptionCategory;

import java.util.List;
import java.util.UUID;

/**
 * Interface defining the input port for category operations in the application layer.
 * It decouples the application logic from the presentation and infrastructure layers.
 */
public interface ApplicationCategoryInputPort {

    /**
     * Creates a new category.
     *
     * @param request the request object containing category details
     * @return the response object containing created category details
     * @throws ApplicationExceptionCategory if an error occurs during category creation
     */
    SharedCategoryResponse createCategory(SharedCreateCategoryRequest request) throws ApplicationExceptionCategory;

    /**
     * Retrieves a list of categories based on the provided filter criteria.
     *
     * @param filter the filter criteria for retrieving categories
     * @return a list of categories matching the filter criteria
     * @throws ApplicationExceptionCategory if an error occurs during retrieval
     */
    List<SharedCategoryResponse> getCategories(SharedCategoryFilterCriteria filter) throws ApplicationExceptionCategory;

    /**
     * Retrieves a category by its unique identifier.
     *
     * @param categoryId the unique identifier of the category
     * @return the response object containing category details
     * @throws ApplicationExceptionCategory if the category is not found or an error occurs
     */
    SharedCategoryResponse getCategoryById(UUID categoryId) throws ApplicationExceptionCategory;

    /**
     * Updates an existing category.
     *
     * @param categoryId the unique identifier of the category to update
     * @param request    the request object containing updated category details
     * @return the response object containing updated category details
     * @throws ApplicationExceptionCategory if the category is not found or an error occurs during update
     */
    SharedCategoryResponse updateCategory(UUID categoryId, SharedUpdateCategoryRequest request) throws ApplicationExceptionCategory;

    /**
     * Deletes a category.
     *
     * @param categoryId the unique identifier of the category to delete
     * @param cascade    flag indicating whether to cascade the delete operation to subcategories
     * @throws ApplicationExceptionCategory if the category is not found or an error occurs during deletion
     */
    void deleteCategory(UUID categoryId, boolean cascade) throws ApplicationExceptionCategory;
}