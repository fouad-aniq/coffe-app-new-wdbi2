package ai.shreds.shared;

public enum EventType {
    CATEGORY_CREATED("category_created"),
    CATEGORY_UPDATED("category_updated"),
    CATEGORY_DELETED("category_deleted");

    private final String value;

    EventType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static EventType fromValue(String value) {
        for (EventType eventType : EventType.values()) {
            if (eventType.value.equals(value)) {
                return eventType;
            }
        }
        throw new IllegalArgumentException("Invalid event type: " + value);
    }
}