package ai.shreds.shared;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;
import ai.shreds.application.validators.ValidCategoryParentValidator; // Added import for the validator

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidCategoryParentValidator.class)
@Documented
public @interface ValidCategoryParent {
    String message() default "Invalid parent category. A category cannot be its own parent or a descendant of itself.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}