package ai.shreds.shared;

import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.List;

public class MetadataValidator {

    private static final Set<String> DISALLOWED_KEYS = new HashSet<>();
    private static final Set<Object> DISALLOWED_VALUES = new HashSet<>();
    private static final Set<Class<?>> ALLOWED_VALUE_TYPES = Set.of(
            String.class,
            Integer.class,
            Long.class,
            Float.class,
            Double.class,
            Boolean.class,
            List.class,
            Map.class
    );

    static {
        DISALLOWED_KEYS.add("password");
        DISALLOWED_KEYS.add("creditCardNumber");
        // Add other disallowed keys as required

        DISALLOWED_VALUES.add("disallowedValue1");
        DISALLOWED_VALUES.add(12345);
        // Add other disallowed values as required
    }

    public static void validateMetadata(Map<String, Object> metadata) throws ValidationException {
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

    public static class ValidationException extends Exception {
        public ValidationException(String message) {
            super(message);
        }
    }
}
