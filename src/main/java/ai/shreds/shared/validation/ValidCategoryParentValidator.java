package ai.shreds.shared;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.UUID;

public class ValidCategoryParentValidator implements ConstraintValidator<ValidCategoryParent, SharedCategoryDTO> {

    @Override
    public void initialize(ValidCategoryParent constraintAnnotation) {
        // No initialization needed for this validator
    }

    @Override
    public boolean isValid(SharedCategoryDTO categoryDTO, ConstraintValidatorContext context) {
        // Return true if the categoryDTO is null, as there's nothing to validate
        if (categoryDTO == null) {
            return true;
        }
        
        UUID categoryId = categoryDTO.getId();
        UUID parentCategoryId = categoryDTO.getParentCategoryId();

        // Check if the category is its own parent
        if (categoryId != null && categoryId.equals(parentCategoryId)) {
            return false; // A category cannot be its own parent
        }

        // Further cyclical checks would require access to the data repository
        return true;
    }
}