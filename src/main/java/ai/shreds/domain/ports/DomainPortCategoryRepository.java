package ai.shreds.domain.ports;

import ai.shreds.domain.entities.DomainEntityCategory;
import ai.shreds.shared.SharedCategoryFilterCriteria;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Defines the data persistence operations for Category entities.
 * Acts as an outbound port in the Hexagonal Architecture,
 * providing an abstraction over the underlying database access,
 * and follows the Repository Pattern to separate data access logic from the domain logic.
 */
public interface DomainPortCategoryRepository {

    /**
     * Saves a new Category entity or updates an existing one.
     * Use the returned instance for further operations as the save operation might have changed the category instance completely.
     *
     * @param category the DomainEntityCategory to save; must not be null.
     * @return the saved DomainEntityCategory; will never be null.
     */
    DomainEntityCategory save(DomainEntityCategory category);

    /**
     * Finds a Category by its unique identifier.
     *
     * @param categoryId the UUID of the category; must not be null.
     * @return an Optional containing the DomainEntityCategory if found, or Optional.empty() if not found.
     */
    Optional<DomainEntityCategory> findById(UUID categoryId);

    /**
     * Retrieves all categories, optionally filtered by SharedCategoryFilterCriteria.
     *
     * @param filter the filtering criteria to apply.
     * @return a list of DomainEntityCategory matching the filter criteria.
     */
    List<DomainEntityCategory> findAll(SharedCategoryFilterCriteria filter);

    /**
     * Finds categories by their parent category ID.
     *
     * @param parentCategoryId the UUID of the parent category; must not be null.
     * @return a list of DomainEntityCategory that have the specified parent.
     */
    List<DomainEntityCategory> findByParentCategoryId(UUID parentCategoryId);

    /**
     * Finds categories associated with any of the specified tags.
     *
     * @param tags a list of tags; must not be null.
     * @return a list of DomainEntityCategory that have any of the specified tags.
     */
    List<DomainEntityCategory> findByTagsIn(List<String> tags);

    /**
     * Deletes a category by its unique identifier.
     *
     * @param categoryId the UUID of the category to delete; must not be null.
     */
    void deleteById(UUID categoryId);

    /**
     * Deletes a category by passing the Category entity.
     *
     * @param category the DomainEntityCategory to delete; must not be null.
     */
    void delete(DomainEntityCategory category);

    /**
     * Checks if a category exists by its unique identifier.
     *
     * @param categoryId the UUID of the category; must not be null.
     * @return true if the category exists, false otherwise.
     */
    boolean existsById(UUID categoryId);

}