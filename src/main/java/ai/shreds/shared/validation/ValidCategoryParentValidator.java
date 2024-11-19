package ai.shreds.shared.validation;

import ai.shreds.domain.ports.DomainPortCategoryRepository;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ValidCategoryParentValidator implements ConstraintValidator<ValidCategoryParent, UUID> {

    private final DomainPortCategoryRepository categoryRepository;

    @Override
    public boolean isValid(UUID parentCategoryId, ConstraintValidatorContext context) {
        if (parentCategoryId == null) {
            return true;
        }
        return categoryRepository.existsById(parentCategoryId);
    }
}