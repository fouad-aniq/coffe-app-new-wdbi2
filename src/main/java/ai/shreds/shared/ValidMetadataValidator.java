package ai.shreds.shared;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

// Import the ValidMetadata annotation
import ai.shreds.shared.ValidMetadata;

public class ValidMetadataValidator implements ConstraintValidator<ValidMetadata, Map<String, Object>> {
    private static final Set<String> DISALLOWED_KEYS = new HashSet<>();
    private static final Set<Object> DISALLOWED_VALUES = new HashSet<>();

    static {
        DISALLOWED_KEYS.add("password");
        DISALLOWED_KEYS.add("secret");
        // Add more disallowed keys if necessary

        DISALLOWED_VALUES.add("disallowedValue1");
        DISALLOWED_VALUES.add("disallowedValue2");
        // Add more disallowed values if necessary
    }

    // Added the missing initialize method required by the ConstraintValidator interface
    @Override
    public void initialize(ValidMetadata constraintAnnotation) {
        // No initialization necessary in this validator
    }

    @Override
    public boolean isValid(Map<String, Object> metadata, ConstraintValidatorContext context) {
        if (metadata == null) {
            return true; // Null metadata is acceptable; use @NotNull to enforce non-null
        }
        return validateMap(metadata, context);
    }

    private boolean validateMap(Map<String, Object> map, ConstraintValidatorContext context) {
        boolean isValid = true;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (key == null || key.trim().isEmpty()) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Metadata key cannot be null or empty").addConstraintViolation();
                isValid = false;
            }

            if (DISALLOWED_KEYS.contains(key)) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Disallowed metadata key: " + key).addConstraintViolation();
                isValid = false;
            }

            if (!isValidValue(value, context)) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Invalid value for metadata key '" + key + "'").addConstraintViolation();
                isValid = false;
            }
        }
        return isValid;
    }

    private boolean isValidValue(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Accept null values; adjust if nulls are disallowed
        }
        if (DISALLOWED_VALUES.contains(value)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Disallowed value in metadata: " + value).addConstraintViolation();
            return false;
        }
        if (value instanceof String || value instanceof Number || value instanceof Boolean) {
            return true;
        }
        if (value instanceof List<?>) {
            return validateList((List<?>) value, context);
        }
        if (value instanceof Map<?, ?>) {
            @SuppressWarnings("unchecked")
            Map<String, Object> nestedMap = (Map<String, Object>) value;
            return validateMap(nestedMap, context);
        }
        // Disallow other types
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate("Invalid metadata value type: " + value.getClass().getName()).addConstraintViolation();
        return false;
    }

    private boolean validateList(List<?> list, ConstraintValidatorContext context) {
        boolean isValid = true;
        for (Object item : list) {
            if (!isValidValue(item, context)) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Invalid value in metadata list").addConstraintViolation();
                isValid = false;
            }
        }
        return isValid;
    }
}