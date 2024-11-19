package ai.shreds.shared;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Component;

@Constraint(validatedBy = ValidMetadataValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidMetadata {
    String message() default "Invalid metadata.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

@Component
public class ValidMetadataValidator implements ConstraintValidator<ValidMetadata, Map<String, Object>> {

    private static final Set<String> DISALLOWED_KEYS = Set.of("password", "creditCardNumber", "disallowedKey1", "disallowedKey2");
    private static final Set<Object> DISALLOWED_VALUES = Set.of("disallowedValue1", "disallowedValue2");

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
            if (value == null) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Metadata value for key '" + key + "' cannot be null.")
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

    public static class InvalidMetadataException extends RuntimeException {
        public InvalidMetadataException(String message) {
            super(message);
        }
    }
}
