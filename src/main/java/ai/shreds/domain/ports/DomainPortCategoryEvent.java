// Full fixed and enhanced code for the main class `DomainPortCategoryEvent`
package ai.shreds.domain.ports;

import ai.shreds.domain.entities.DomainEntityCategory;
import java.util.UUID;

/**
 * Interface for publishing category-related events when categories are created, updated, or deleted.
 * This allows the domain layer to be decoupled from the infrastructure layer by relying on abstractions.
 * Implementations of this interface will handle the publishing logic to external systems.
 * Business logic: Upon any change to a category, appropriate events must be published to notify other services of the change.
 */
public interface DomainPortCategoryEvent {

    /**
     * Publishes an event indicating that a category has been created.
     *
     * @param category the category that was created
     */
    void publishCategoryCreatedEvent(DomainEntityCategory category);

    /**
     * Publishes an event indicating that a category has been updated.
     *
     * @param category the category that was updated
     */
    void publishCategoryUpdatedEvent(DomainEntityCategory category);

    /**
     * Publishes an event indicating that a category has been deleted.
     *
     * @param categoryId the ID of the category that was deleted
     */
    void publishCategoryDeletedEvent(UUID categoryId);
}