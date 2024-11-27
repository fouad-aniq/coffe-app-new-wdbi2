package ai.shreds.application.ports;

import ai.shreds.shared.SharedCategoryEvent;
import java.util.UUID;

/**
 * ApplicationCategoryEventOutputPort defines methods to publish category-related events
 * to notify other services of changes to categories.
 *
 * <p>Methods include publishing created, updated, and deleted category events.
 * Implementations should manage error handling to prevent lost events.</p>
 */
public interface ApplicationCategoryEventOutputPort {

    /**
     * Publishes a 'category_created' event.
     * 
     * @param event the SharedCategoryEvent containing event details and category data.
     */
    void publishCategoryCreatedEvent(SharedCategoryEvent event);

    /**
     * Publishes a 'category_updated' event.
     * 
     * @param event the SharedCategoryEvent containing event details and updated category data.
     */
    void publishCategoryUpdatedEvent(SharedCategoryEvent event);

    /**
     * Publishes a 'category_deleted' event.
     * 
     * @param categoryId the UUID of the deleted category.
     */
    void publishCategoryDeletedEvent(UUID categoryId);
}