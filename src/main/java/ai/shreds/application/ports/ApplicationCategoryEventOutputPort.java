package ai.shreds.application.ports;

import ai.shreds.shared.SharedCategoryEvent;
import java.util.UUID;

public interface ApplicationCategoryEventOutputPort {

    /**
     * Publishes an event indicating that a category has been created.
     *
     * @param event the event data containing details about the created category
     */
    void publishCategoryCreatedEvent(SharedCategoryEvent event);

    /**
     * Publishes an event indicating that a category has been updated.
     *
     * @param event the event data containing details about the updated category
     */
    void publishCategoryUpdatedEvent(SharedCategoryEvent event);

    /**
     * Publishes an event indicating that a category has been deleted.
     *
     * @param categoryId the UUID of the deleted category
     */
    void publishCategoryDeletedEvent(UUID categoryId);
}