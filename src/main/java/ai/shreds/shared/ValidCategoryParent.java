
package ai.shreds.shared;

import ai.shreds.application.validators.ValidCategoryParentValidator;
import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidCategoryParentValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidCategoryParent {
    String message() default "Invalid parent category. A category cannot be its own parent or a descendant of itself.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
