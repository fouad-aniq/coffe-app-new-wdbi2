package ai.shreds.shared;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents category-related events that are published to Apache Kafka to notify other services of changes to categories.
 * Includes the event type, timestamp, and the category data relevant to the event.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SharedCategoryEvent {
    /**
     * The type of event (e.g., 'CREATED', 'UPDATED', 'DELETED').
     */
    private String eventType;

    /**
     * The timestamp when the event occurred.
     */
    private Timestamp timestamp;

    /**
     * The category data relevant to the event.
     */
    private SharedCategoryDTO category;
}
