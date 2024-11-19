package ai.shreds.shared;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidCategoryParentValidator.class)
@Documented
public @interface ValidCategoryParent {
    String message() default "Invalid parent category. A category cannot be its own parent or a descendant of itself.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

package ai.shreds.shared;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.UUID;

public class ValidCategoryParentValidator implements ConstraintValidator<ValidCategoryParent, SharedCategoryDTO> {

    @Override
    public boolean isValid(SharedCategoryDTO categoryDTO, ConstraintValidatorContext context) {
        if (categoryDTO == null) {
            return true;
        }
        UUID categoryId = categoryDTO.getId();
        UUID parentCategoryId = categoryDTO.getParentCategoryId();

        if (categoryId != null && categoryId.equals(parentCategoryId)) {
            return false; // A category cannot be its own parent
        }

        // Cannot perform further cyclical checks without access to data
        return true;
    }
}
