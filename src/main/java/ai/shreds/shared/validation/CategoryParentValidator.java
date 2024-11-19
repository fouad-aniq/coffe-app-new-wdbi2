package ai.shreds.shared.validation;

import ai.shreds.domain.ports.DomainPortCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.UUID;

@Component
public class CategoryParentValidator implements ConstraintValidator<ValidCategoryParent, UUID> {

    @Autowired
    private DomainPortCategoryRepository categoryRepository;

    @Override
    public void initialize(ValidCategoryParent constraintAnnotation) {}

    @Override
    public boolean isValid(UUID parentCategoryId, ConstraintValidatorContext context) {
        if (parentCategoryId == null) {
            return true; // Parent category is optional
        }

        // Check if parent category exists
        return categoryRepository.existsById(parentCategoryId);
    }
}