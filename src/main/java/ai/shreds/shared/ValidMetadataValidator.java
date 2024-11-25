package ai.shreds.shared;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;

@Component
public class ValidMetadataValidator implements ConstraintValidator<ValidMetadata, Map<String, Object>> {

    private static final Set<String> DISALLOWED_KEYS = new HashSet<>(Arrays.asList(\"password\", \"creditCardNumber\", \"disallowedKey1\", \"disallowedKey2\"));
    private static final Set<Object> DISALLOWED_VALUES = new HashSet<>(Arrays.asList(\"disallowedValue1\", \"disallowedValue2\"));

    @Override
    public boolean isValid(Map<String, Object> metadata, ConstraintValidatorContext context) {
        if (metadata == null || metadata.isEmpty()) {
            return true; // Assuming null or empty metadata is acceptable
        }
        boolean isValid = true;
        for (Map.Entry<String, Object> entry : metadata.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (key == null || key.isBlank()) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(\"Metadata key cannot be null or blank.\")
                       .addConstraintViolation();
                isValid = false;
            }
            if (DISALLOWED_KEYS.contains(key)) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(\"Metadata contains disallowed key: \" + key)
                       .addConstraintViolation();
                isValid = false;
            }
            if (value == null) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(\"Metadata value for key '\" + key + \"' cannot be null or blank.\")
                       .addConstraintViolation();
                isValid = false;
            }
            if (DISALLOWED_VALUES.contains(value)) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(\"Metadata contains disallowed value: \" + value)
                       .addConstraintViolation();
                isValid = false;
            }
            // Additional structure validation logic can be added here
        }
        return isValid;
    }

    public static class InvalidMetadataException extends RuntimeException {
        public InvalidMetadataException(String message) {
            super(message);
        }
    }
}