package ai.shreds.domain.ports;

import ai.shreds.domain.entities.DomainEntityCategory;
import ai.shreds.shared.SharedCategoryFilterCriteria;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for managing DomainEntityCategory entities.
 */
public interface DomainPortCategoryRepository {

    /**
     * Saves a category entity.
     * @param category the category to save
     * @return the saved category
     */
    DomainEntityCategory save(DomainEntityCategory category);

    /**
     * Finds a category by its ID.
     * @param categoryId the ID of the category
     * @return an Optional containing the found category or empty if not found
     */
    Optional<DomainEntityCategory> findById(UUID categoryId);

    /**
     * Finds all categories matching the given filter criteria.
     * @param filter the filter criteria
     * @return a list of matching categories
     */
    List<DomainEntityCategory> findAll(SharedCategoryFilterCriteria filter);

    /**
     * Finds categories by their parent category ID.
     * @param parentCategoryId the parent category ID
     * @return a list of subcategories
     */
    List<DomainEntityCategory> findByParentCategoryId(UUID parentCategoryId);

    /**
     * Finds categories that have any of the specified tags.
     * @param tags the tags to search for
     * @return a list of categories with matching tags
     */
    List<DomainEntityCategory> findByTagsIn(List<String> tags);

    /**
     * Deletes a category by its ID.
     * @param categoryId the ID of the category to delete
     */
    void deleteById(UUID categoryId);

    /**
     * Deletes a category entity.
     * @param category the category to delete
     */
    void delete(DomainEntityCategory category);

    /**
     * Checks if a category exists by its ID.
     * @param categoryId the ID of the category
     * @return true if the category exists, false otherwise
     */
    boolean existsById(UUID categoryId);
}