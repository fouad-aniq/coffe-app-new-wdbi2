package ai.shreds.shared;

import java.sql.Timestamp;

/**
 * Represents the structure of category event messages published to Apache Kafka
 * to notify other services whenever a category is created, updated, or deleted.
 * Includes the event type, timestamp, and relevant category data.
 */
public class SharedCategoryEvent {

    /**
     * The type of the event, e.g., "CREATED", "UPDATED", or "DELETED".
     */
    private String eventType;

    /**
     * The timestamp when the event was created.
     */
    private Timestamp timestamp;

    /**
     * The category data relevant to the event.
     */
    private SharedCategoryDTO category;

    // Constructors

    public SharedCategoryEvent() {
    }

    public SharedCategoryEvent(String eventType, Timestamp timestamp, SharedCategoryDTO category) {
        this.eventType = eventType;
        this.timestamp = timestamp;
        this.category = category;
    }

    // Getters and Setters

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public SharedCategoryDTO getCategory() {
        return category;
    }

    public void setCategory(SharedCategoryDTO category) {
        this.category = category;
    }
}