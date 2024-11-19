package ai.shreds.shared.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Map;
import java.util.List;

public class ValidMetadataValidator implements ConstraintValidator<ValidMetadata, Map<String, Object>> {

    @Override
    public void initialize(ValidMetadata constraintAnnotation) {
        // No initialization needed for this validator
    }

    @Override
    public boolean isValid(Map<String, Object> metadata, ConstraintValidatorContext context) {
        // If metadata is null, consider it valid (use @NotNull to enforce non-null)
        if (metadata == null) {
            return true;
        }

        // Validate each entry in the metadata map
        for (Map.Entry<String, Object> entry : metadata.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            // Check that key is not null or empty
            if (key == null || key.trim().isEmpty()) {
                return false;
            }

            // Check that value is of an acceptable type
            if (!(value instanceof String || value instanceof Number || value instanceof Boolean ||
                    value instanceof Map || value instanceof List)) {
                return false;
            }
        }

        // Metadata is valid
        return true;
    }
}