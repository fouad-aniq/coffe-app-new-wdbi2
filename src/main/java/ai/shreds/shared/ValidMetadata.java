package ai.shreds.shared;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

/**
 * Custom validation annotation for metadata fields.
 * Annotate metadata fields with @ValidMetadata to apply custom validation logic.
 */
@Constraint(validatedBy = ValidMetadataValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidMetadata {
    String message() default "Invalid metadata.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}