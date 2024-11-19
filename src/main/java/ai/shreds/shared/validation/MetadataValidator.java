package ai.shreds.shared.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Map;
import java.util.List;
import java.util.Arrays;

public class MetadataValidator implements ConstraintValidator<ValidMetadata, Map<String, Object>> {

    // Define a list of disallowed keys
    private static final List<String> DISALLOWED_KEYS = Arrays.asList("password", "secret");

    @Override
    public void initialize(ValidMetadata constraintAnnotation) {
        // No initialization needed in this case
    }

    @Override
    public boolean isValid(Map<String, Object> metadata, ConstraintValidatorContext context) {
        // If metadata is null or empty, assume valid
        if (metadata == null || metadata.isEmpty()) {
            return true;
        }

        boolean isValid = true;

        // Iterate over the keys in the metadata map
        for (String key : metadata.keySet()) {
            // Check if the key is in the list of disallowed keys
            if (DISALLOWED_KEYS.contains(key)) {
                // Add a constraint violation message
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                        "Metadata contains disallowed key: " + key)
                        .addConstraintViolation();
                isValid = false;
            }
        }

        return isValid;
    }
}