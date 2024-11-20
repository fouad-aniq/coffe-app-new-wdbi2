package ai.shreds.shared;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ai.shreds.domain.ports.DomainPortCategoryRepository;
import java.util.UUID;
import java.util.HashSet;
import java.util.Set;

import ai.shreds.shared.SharedCreateCategoryRequest;
import ai.shreds.shared.SharedUpdateCategoryRequest;
import ai.shreds.shared.ValidCategoryParent;

@Component
public class ValidCategoryParentValidator implements ConstraintValidator<ValidCategoryParent, Object> {

    @Autowired
    private DomainPortCategoryRepository categoryRepository;

    @Override
    public void initialize(ValidCategoryParent constraintAnnotation) {
        // No initialization required
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        UUID parentCategoryId = null;

        if (value instanceof SharedCreateCategoryRequest) {
            SharedCreateCategoryRequest request = (SharedCreateCategoryRequest) value;
            parentCategoryId = request.getParentCategoryId();

            // If parentCategoryId is null, no validation needed
            if (parentCategoryId == null) {
                return true;
            }

            // Validate that parentCategoryId exists
            if (!categoryRepository.existsById(parentCategoryId)) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Parent category does not exist.")
                        .addPropertyNode("parentCategoryId")
                        .addConstraintViolation();
                return false;
            }

        } else if (value instanceof SharedUpdateCategoryRequest) {
            SharedUpdateCategoryRequest request = (SharedUpdateCategoryRequest) value;
            parentCategoryId = request.getParentCategoryId();

            // If parentCategoryId is null, no validation needed
            if (parentCategoryId == null) {
                return true;
            }

            // Validate that parentCategoryId exists
            if (!categoryRepository.existsById(parentCategoryId)) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Parent category does not exist.")
                        .addPropertyNode("parentCategoryId")
                        .addConstraintViolation();
                return false;
            }

        } else {
            // If the object is not an instance of expected types, consider it valid
            return true;
        }

        // All validations passed
        return true;
    }
}