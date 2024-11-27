// Full fixed and enhanced code for the main class 'ValidMetadata'
package ai.shreds.shared;

import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.regex.Pattern;

public class ValidMetadata {

    // Private constructor to prevent instantiation of utility class
    private ValidMetadata() {
    }

    private static final Set<String> DISALLOWED_KEYS = new HashSet<>();
    private static final Set<Object> DISALLOWED_VALUES = new HashSet<>();
    private static final Pattern KEY_PATTERN = Pattern.compile("^[a-zA-Z0-9_]+$");

    static {
        // Initialize disallowed keys
        DISALLOWED_KEYS.add("password");
        DISALLOWED_KEYS.add("secret");
        DISALLOWED_KEYS.add("token");
        // Add more disallowed keys if necessary

        // Initialize disallowed values
        DISALLOWED_VALUES.add("null");
        DISALLOWED_VALUES.add(""); // empty string
        // Add more disallowed values if necessary
    }

    public static void validateMetadata(Map<String, Object> metadata) throws InvalidMetadataException {
        if (metadata == null) {
            throw new InvalidMetadataException("Metadata cannot be null.");
        }

        for (Map.Entry<String, Object> entry : metadata.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            // Check for disallowed keys
            if (DISALLOWED_KEYS.contains(key)) {
                throw new InvalidMetadataException("Metadata contains disallowed key: " + key);
            }

            // Check key format
            if (!KEY_PATTERN.matcher(key).matches()) {
                throw new InvalidMetadataException("Metadata contains invalid key format: " + key);
            }

            // Check value is not null
            if (value == null) {
                throw new InvalidMetadataException("Metadata value for key '" + key + "' is null");
            }

            // Check for disallowed values
            if (DISALLOWED_VALUES.contains(value)) {
                throw new InvalidMetadataException("Metadata contains disallowed value for key '" + key + "': " + value);
            }

            // Additional value validation can be added here
            // e.g., check value types, nested structures, etc.
        }
    }
}