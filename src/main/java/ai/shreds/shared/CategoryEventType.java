
package ai.shreds.shared;

/**
 * Enum representing the types of category events.
 * Each event type is associated with a specific string value used for event identification.
 */
public enum CategoryEventType {
    
    /**
     * Indicates that a category has been created.
     */
    CREATED("category_created"),

    /**
     * Indicates that a category has been updated.
     */
    UPDATED("category_updated"),

    /**
     * Indicates that a category has been deleted.
     */
    DELETED("category_deleted");

    private final String type;

    /**
     * Constructor to associate the enum constant with its string value.
     *
     * @param type the string representation of the event type
     */
    CategoryEventType(String type) {
        this.type = type;
    }

    /**
     * Retrieves the string value associated with the event type.
     *
     * @return the string representation of the event type
     */
    public String getType() {
        return type;
    }
}
