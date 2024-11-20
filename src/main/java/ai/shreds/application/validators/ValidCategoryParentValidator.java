package ai.shreds.application.validators;

import ai.shreds.shared.ValidCategoryParent;
import ai.shreds.shared.SharedCreateCategoryRequest;
import ai.shreds.domain.services.DomainServiceCategoryManager;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ValidCategoryParentValidator implements ConstraintValidator<ValidCategoryParent, SharedCreateCategoryRequest> {

    private final DomainServiceCategoryManager categoryManager;

    @Override
    public boolean isValid(SharedCreateCategoryRequest request, ConstraintValidatorContext context) {
        UUID parentCategoryId = request.getParentCategoryId();

        if (parentCategoryId == null) {
            return true; // No parent, valid
        }

        // Ensure the parent category ID is valid and does not lead to a cyclical hierarchy
        return categoryManager.isValidParentCategory(null, parentCategoryId);
    }
}