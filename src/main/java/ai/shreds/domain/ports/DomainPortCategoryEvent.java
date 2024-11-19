package ai.shreds.domain.ports;

import ai.shreds.domain.entities.DomainEntityCategory;
import java.util.UUID;

/**
 * Outbound port interface for publishing category-related events to external systems, specifically Apache Kafka.
 * Defines methods for notifying other services when categories are created, updated, or deleted.
 * Supports the Observer/Event Publisher pattern and aligns with transactional outbox patterns to ensure reliable message delivery,
 * and handles failures by implementing retry mechanisms and error handling.
 */
public interface DomainPortCategoryEvent {

    /**
     * Publishes an event indicating that a category has been created.
     * The event includes details such as the event type, timestamp, and relevant category data.
     * Handles failures in publishing events to Kafka by implementing retry mechanisms and error handling.
     *
     * @param category the domain entity representing the newly created category
     * @throws EventPublishingException if the event could not be published
     */
    void publishCategoryCreatedEvent(DomainEntityCategory category) throws EventPublishingException;

    /**
     * Publishes an event indicating that a category has been updated.
     * The event includes details such as the event type, timestamp, and relevant category data.
     * Handles failures in publishing events to Kafka by implementing retry mechanisms and error handling.
     *
     * @param category the domain entity representing the updated category
     * @throws EventPublishingException if the event could not be published
     */
    void publishCategoryUpdatedEvent(DomainEntityCategory category) throws EventPublishingException;

    /**
     * Publishes an event indicating that a category has been deleted.
     * The event includes details such as the event type, timestamp, and the ID of the deleted category.
     * Handles failures in publishing events to Kafka by implementing retry mechanisms and error handling.
     *
     * @param categoryId the UUID of the category that was deleted
     * @throws EventPublishingException if the event could not be published
     */
    void publishCategoryDeletedEvent(UUID categoryId) throws EventPublishingException;
}
