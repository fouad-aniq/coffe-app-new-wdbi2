package ai.shreds.shared;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import org.springframework.stereotype.Component;
import ai.shreds.shared.ValidMetadata;

/**
 * Validator class for the @ValidMetadata annotation.
 * Validates that the metadata map does not contain disallowed keys or values,
 * and that keys and values are not null or empty.
 */
@Component
public class ValidMetadataValidator implements ConstraintValidator<ValidMetadata, Map<String, Object>> {

    // Replaced Set.of(...) with Java 8 compatible code
    private static final Set<String> DISALLOWED_KEYS = new HashSet<>(Arrays.asList("password", "creditCardNumber", "disallowedKey1", "disallowedKey2"));
    private static final Set<Object> DISALLOWED_VALUES = new HashSet<>(Arrays.asList("disallowedValue1", "disallowedValue2"));

    @Override
    public boolean isValid(Map<String, Object> metadata, ConstraintValidatorContext context) {
        if (metadata == null || metadata.isEmpty()) {
            return true; // Assuming null or empty metadata is acceptable
        }
        boolean isValid = true;
        for (Map.Entry<String, Object> entry : metadata.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            // Replaced key.isBlank() with key.trim().isEmpty() for Java 8 compatibility
            if (key == null || key.trim().isEmpty()) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Metadata key cannot be null or blank.")
                       .addConstraintViolation();
                isValid = false;
            }
            if (DISALLOWED_KEYS.contains(key)) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Metadata contains disallowed key: " + key)
                       .addConstraintViolation();
                isValid = false;
            }
            // Check if value is null or, if it's a String, if it's empty or blank
            if (value == null || (value instanceof String && ((String) value).trim().isEmpty())) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Metadata value for key '" + key + "' cannot be null or blank.")
                       .addConstraintViolation();
                isValid = false;
            }
            if (DISALLOWED_VALUES.contains(value)) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Metadata contains disallowed value: " + value)
                       .addConstraintViolation();
                isValid = false;
            }
            // Additional structure validation logic can be added here
        }
        return isValid;
    }
}