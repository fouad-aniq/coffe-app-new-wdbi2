package ai.shreds.shared;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;
import java.util.Arrays;

@Data
@NoArgsConstructor
public class SharedCategoryEvent {

    private String eventType;
    private Timestamp timestamp;
    private SharedCategoryDTO category;

    private static final List<String> VALID_EVENT_TYPES = Arrays.asList(
            "category_created",
            "category_updated",
            "category_deleted"
    );

    public SharedCategoryEvent(String eventType, Timestamp timestamp, SharedCategoryDTO category) {
        setEventType(eventType);
        this.timestamp = timestamp;
        this.category = category;
    }

    public void setEventType(String eventType) {
        if (!VALID_EVENT_TYPES.contains(eventType)) {
            throw new IllegalArgumentException("Invalid event type: " + eventType);
        }
        this.eventType = eventType;
    }
}