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

import javax.validation.Validator;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.*;
import java.util.UUID;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ApplicationCategoryService implements ApplicationCategoryInputPort {

    private final DomainPortCategoryRepository categoryRepository;
    private final DomainPortCategoryEvent categoryEventPublisher;
    private final ApplicationCategoryMapper categoryMapper;
    private final Validator validator;

    @Override
    public SharedCategoryResponse createCategory(SharedCreateCategoryRequest request) {
        // Validate input
        validateRequest(request);

        DomainEntityCategory parentCategory = null;
        UUID parentCategoryId = request.getParentCategoryId();

        if (parentCategoryId != null) {
            // Retrieve parent category
            Optional<DomainEntityCategory> parentOpt = categoryRepository.findById(parentCategoryId);
            if (!parentOpt.isPresent()) {
                throw new ApplicationExceptionCategory("Parent category not found.", 404);
            }
            parentCategory = parentOpt.get();

            // Check for cyclical hierarchies (category cannot be its own ancestor)
            if (isDescendant(parentCategory, parentCategory)) {
                throw new ApplicationExceptionCategory("Category cannot be its own parent.", 400);
            }
        }

        // Check for unique name under same parent
        List<DomainEntityCategory> siblingCategories = categoryRepository.findByParentCategoryId(parentCategoryId);
        for (DomainEntityCategory sibling : siblingCategories) {
            if (sibling.getName().equalsIgnoreCase(request.getName())) {
                throw new ApplicationExceptionCategory("Category with the same name already exists under the specified parent.", 400);
            }
        }

        // Map request to domain entity
        DomainEntityCategory category = categoryMapper.toDomain(request);
        category.setParentCategory(parentCategory);
        category.setId(UUID.randomUUID());
        category.setCreatedAt(Instant.now());
        category.setUpdatedAt(Instant.now());

        // Save category
        category = categoryRepository.save(category);

        // Publish event
        categoryEventPublisher.publishCategoryCreatedEvent(category);

        // Map to response DTO
        SharedCategoryResponse response = categoryMapper.toDTO(category);

        return response;
    }

    @Override
    public List<SharedCategoryResponse> getCategories(SharedCategoryFilterCriteria filter) {
        // Retrieve categories from repository
        List<DomainEntityCategory> categories = categoryRepository.findAll(filter);

        // Map to response DTOs
        List<SharedCategoryResponse> responses = categoryMapper.toDTOList(categories);

        return responses;
    }

    @Override
    public SharedCategoryResponse getCategoryById(UUID categoryId) {
        // Retrieve category
        Optional<DomainEntityCategory> categoryOpt = categoryRepository.findById(categoryId);
        if (!categoryOpt.isPresent()) {
            throw new ApplicationExceptionCategory("Category not found.", 404);
        }

        DomainEntityCategory category = categoryOpt.get();

        // Map to response DTO
        SharedCategoryResponse response = categoryMapper.toDTO(category);

        return response;
    }

    @Override
    public SharedCategoryResponse updateCategory(UUID categoryId, SharedUpdateCategoryRequest request) {
        // Validate input
        validateRequest(request);

        // Retrieve existing category
        Optional<DomainEntityCategory> categoryOpt = categoryRepository.findById(categoryId);
        if (!categoryOpt.isPresent()) {
            throw new ApplicationExceptionCategory("Category not found.", 404);
        }

        DomainEntityCategory category = categoryOpt.get();

        // Get new parent category id from request
        UUID newParentCategoryId = request.getParentCategoryId();
        DomainEntityCategory newParentCategory = null;

        // If parent category is changed
        if (newParentCategoryId != null && (category.getParentCategory() == null || !newParentCategoryId.equals(category.getParentCategory().getId()))) {
            // Retrieve new parent category
            Optional<DomainEntityCategory> newParentOpt = categoryRepository.findById(newParentCategoryId);
            if (!newParentOpt.isPresent()) {
                throw new ApplicationExceptionCategory("New parent category not found.", 404);
            }
            newParentCategory = newParentOpt.get();

            // Check for cyclic hierarchy
            if (isDescendant(category, newParentCategory)) {
                throw new ApplicationExceptionCategory("Cyclic hierarchy detected.", 400);
            }

            category.setParentCategory(newParentCategory);
        }

        UUID parentCategoryId = category.getParentCategory() != null ? category.getParentCategory().getId() : null;

        // Check for unique name under same parent
        List<DomainEntityCategory> siblingCategories = categoryRepository.findByParentCategoryId(parentCategoryId);
        for (DomainEntityCategory sibling : siblingCategories) {
            if (!sibling.getId().equals(categoryId) && sibling.getName().equalsIgnoreCase(request.getName())) {
                throw new ApplicationExceptionCategory("Category with the same name already exists under the specified parent.", 400);
            }
        }

        // Update category fields from request
        category = categoryMapper.toDomain(request, category);
        category.setUpdatedAt(Instant.now());

        // Save category
        category = categoryRepository.save(category);

        // Publish event
        categoryEventPublisher.publishCategoryUpdatedEvent(category);

        // Map to response DTO
        SharedCategoryResponse response = categoryMapper.toDTO(category);

        return response;
    }

    @Override
    public void deleteCategory(UUID categoryId, boolean cascade) {
        // Retrieve category
        Optional<DomainEntityCategory> categoryOpt = categoryRepository.findById(categoryId);
        if (!categoryOpt.isPresent()) {
            throw new ApplicationExceptionCategory("Category not found.", 404);
        }

        DomainEntityCategory category = categoryOpt.get();

        if (cascade) {
            // Delete all subcategories recursively
            deleteSubcategories(category);
        } else {
            // Check if category has subcategories
            if (category.getSubcategories() != null && !category.getSubcategories().isEmpty()) {
                throw new ApplicationExceptionCategory("Category has subcategories. Use cascade delete.", 400);
            }
        }

        // Delete category
        categoryRepository.delete(category);

        // Publish event
        categoryEventPublisher.publishCategoryDeletedEvent(categoryId);
    }

    private void validateRequest(Object request) {
        Set<ConstraintViolation<Object>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

    private boolean isDescendant(DomainEntityCategory category, DomainEntityCategory potentialAncestor) {
        // Recursive check if potentialAncestor is a descendant of category
        if (potentialAncestor == null) {
            return false;
        }
        if (potentialAncestor.getId().equals(category.getId())) {
            return true;
        }
        return isDescendant(category, potentialAncestor.getParentCategory());
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
