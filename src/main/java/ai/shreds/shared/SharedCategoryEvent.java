package ai.shreds.shared;

// Necessary imports for Timestamp and Lombok annotations
import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents category-related events that are published to Apache Kafka to notify other services of changes to categories.
 * Includes the event type, timestamp, and the category data relevant to the event.
 */
@Data // Lombok annotation to generate getters, setters, equals, hashCode, toString
@Builder // Lombok annotation to enable the builder pattern
@NoArgsConstructor // Lombok annotation to generate a no-args constructor
@AllArgsConstructor // Lombok annotation to generate an all-args constructor
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