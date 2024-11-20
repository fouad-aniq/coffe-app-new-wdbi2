package ai.shreds.shared;

import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.HashSet;
import java.util.Arrays;
import java.util.Collections;
import lombok.experimental.UtilityClass;
import ai.shreds.shared.ValidationException;

/**
 * Utility class for validating metadata in shared category requests and DTOs.
 */
@UtilityClass
public class MetadataValidator {

    private static final Set<String> DISALLOWED_KEYS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            "password",
            "creditCardNumber"
            // Add other disallowed keys as required
    )));
    private static final Set<Object> DISALLOWED_VALUES = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            "disallowedValue1",
            12345
            // Add other disallowed values as required
    )));
    private static final Set<Class<?>> ALLOWED_VALUE_TYPES = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            String.class,
            Integer.class,
            Long.class,
            Float.class,
            Double.class,
            Boolean.class,
            List.class,
            Map.class
    )));

    /**
     * Validates the provided metadata map.
     *
     * @param metadata the metadata map to validate
     * @throws ValidationException if validation fails
     */
    public void validateMetadata(Map<String, Object> metadata) throws ValidationException {
        if (metadata == null || metadata.isEmpty()) {
            return; // Nothing to validate
        }

        for (Map.Entry<String, Object> entry : metadata.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (DISALLOWED_KEYS.contains(key)) {
                throw new ValidationException("Metadata contains disallowed key: " + key);
            }

            if (DISALLOWED_VALUES.contains(value)) {
                throw new ValidationException("Metadata contains disallowed value: " + value);
            }

            boolean allowedType = false;
            for (Class<?> allowedClass : ALLOWED_VALUE_TYPES) {
                if (allowedClass.isInstance(value)) {
                    allowedType = true;
                    break;
                }
            }

            if (!allowedType) {
                throw new ValidationException("Metadata contains invalid value type for key: " + key);
            }
        }
    }
}