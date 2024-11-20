package ai.shreds.application.services;

import ai.shreds.application.exceptions.ApplicationExceptionCategory;
import ai.shreds.application.ports.ApplicationCategoryInputPort;
import ai.shreds.application.utils.ApplicationCategoryMapper;
import ai.shreds.domain.entities.DomainEntityCategory;
import ai.shreds.domain.ports.DomainPortCategoryEvent;
import ai.shreds.domain.ports.DomainPortCategoryRepository;
import ai.shreds.shared.SharedCategoryFilterCriteria;
import ai.shreds.shared.SharedCategoryResponse;
import ai.shreds.shared.SharedCreateCategoryRequest;
import ai.shreds.shared.SharedUpdateCategoryRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApplicationCategoryService implements ApplicationCategoryInputPort {

    private final DomainPortCategoryRepository categoryRepository;
    private final DomainPortCategoryEvent categoryEventPublisher;
    private final ApplicationCategoryMapper categoryMapper;
    private final Validator validator;

    @Override
    public SharedCategoryResponse createCategory(SharedCreateCategoryRequest request) {
        validateRequest(request);

        UUID parentCategoryId = request.getParentCategoryId();
        DomainEntityCategory parentCategory = null;

        if (parentCategoryId != null) {
            parentCategory = categoryRepository.findById(parentCategoryId)
                    .orElseThrow(() -> new ApplicationExceptionCategory("Parent category not found.", 404));
        }

        DomainEntityCategory category = categoryMapper.toDomain(request);
        category.setParentCategory(parentCategory);
        Timestamp now = new Timestamp(System.currentTimeMillis());
        category.setCreatedAt(now);
        category.setUpdatedAt(now);

        category = categoryRepository.save(category);

        categoryEventPublisher.publishCategoryCreatedEvent(category);

        return categoryMapper.toDTO(category);
    }

    @Override
    public List<SharedCategoryResponse> getCategories(SharedCategoryFilterCriteria filter) {
        List<DomainEntityCategory> categories = categoryRepository.findAll(filter);
        return categoryMapper.toDTOList(categories);
    }

    @Override
    public SharedCategoryResponse getCategoryById(UUID categoryId) {
        DomainEntityCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ApplicationExceptionCategory("Category not found.", 404));

        return categoryMapper.toDTO(category);
    }

    @Override
    public SharedCategoryResponse updateCategory(UUID categoryId, SharedUpdateCategoryRequest request) {
        validateRequest(request);

        DomainEntityCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ApplicationExceptionCategory("Category not found.", 404));

        category = categoryMapper.toDomain(request, category);
        Timestamp now = new Timestamp(System.currentTimeMillis());
        category.setUpdatedAt(now);

        UUID newParentCategoryId = request.getParentCategoryId();
        UUID currentParentCategoryId = category.getParentCategory() != null ? category.getParentCategory().getId() : null;

        if (newParentCategoryId != null && !newParentCategoryId.equals(currentParentCategoryId)) {
            DomainEntityCategory newParentCategory = categoryRepository.findById(newParentCategoryId)
                    .orElseThrow(() -> new ApplicationExceptionCategory("New parent category not found.", 404));
            category.setParentCategory(newParentCategory);
        } else if (newParentCategoryId == null && currentParentCategoryId != null) {
            category.setParentCategory(null);
        }

        category = categoryRepository.save(category);

        categoryEventPublisher.publishCategoryUpdatedEvent(category);

        return categoryMapper.toDTO(category);
    }

    @Override
    public void deleteCategory(UUID categoryId, boolean cascade) {
        DomainEntityCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ApplicationExceptionCategory("Category not found.", 404));

        if (cascade) {
            deleteSubcategories(category);
        } else if (category.getSubcategories() != null && !category.getSubcategories().isEmpty()) {
            throw new ApplicationExceptionCategory("Category has subcategories. Use cascade delete.", 400);
        }

        categoryRepository.delete(category);

        categoryEventPublisher.publishCategoryDeletedEvent(categoryId);
    }

    private void validateRequest(Object request) {
        Set<ConstraintViolation<Object>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

    private void deleteSubcategories(DomainEntityCategory category) {
        List<DomainEntityCategory> subcategories = categoryRepository.findByParentCategoryId(category.getId());
        for (DomainEntityCategory subcategory : subcategories) {
            deleteSubcategories(subcategory);
            categoryRepository.delete(subcategory);
            categoryEventPublisher.publishCategoryDeletedEvent(subcategory.getId());
        }
    }
}