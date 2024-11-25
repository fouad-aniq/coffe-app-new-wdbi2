package ai.shreds.shared;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;
import ai.shreds.application.validators.ValidCategoryParentValidator;

/**
 * Custom annotation for validating that a category's parent is valid.
 */
@Constraint(validatedBy = ValidCategoryParentValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidCategoryParent {
    String message() default \"Invalid parent category. A category cannot be its own parent or a descendant of itself.\";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}