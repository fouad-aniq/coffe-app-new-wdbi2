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
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class ApplicationCategoryService implements ApplicationCategoryInputPort {

    private static final int MAX_HIERARCHY_DEPTH = 10;

    private final ApplicationCategoryMapper categoryMapper;
    private final DomainPortCategoryRepository categoryRepository;
    private final DomainPortCategoryEvent categoryEventPublisher;

    @Override
    public SharedCategoryResponse createCategory(@Valid SharedCreateCategoryRequest request) {
        // Ensure tags are unique within the category
        if (request.getTags() != null && !areTagsUnique(request.getTags())) {
            throw new ApplicationExceptionCategory("Tags must be unique within the category.", HttpStatus.BAD_REQUEST);
        }

        // Validate metadata
        validateMetadata(request.getMetadata());

        // Validate parent category existence and prevent cyclical relationships
        DomainEntityCategory parentCategory = null;
        if (request.getParentCategoryId() != null) {
            parentCategory = categoryRepository.findById(request.getParentCategoryId())
                    .orElseThrow(() -> new ApplicationExceptionCategory("Parent category not found.", HttpStatus.NOT_FOUND));
            // Check hierarchy depth
            int depth = getHierarchyDepth(parentCategory);
            if (depth >= MAX_HIERARCHY_DEPTH) {
                throw new ApplicationExceptionCategory("Category hierarchy depth cannot exceed " + MAX_HIERARCHY_DEPTH + ".", HttpStatus.BAD_REQUEST);
            }
        }

        // Map request to domain entity
        DomainEntityCategory category = categoryMapper.toDomain(request);
        category.setParentCategory(parentCategory);
        category.setCreatedAt(Instant.now());
        category.setUpdatedAt(Instant.now());

        // Persist the new category using the CategoryRepository
        DomainEntityCategory savedCategory;
        try {
            savedCategory = categoryRepository.save(category);
        } catch (OptimisticLockingFailureException e) {
            throw new ApplicationExceptionCategory("Conflict occurred while saving the category. Please retry.", HttpStatus.CONFLICT);
        }

        // Publish category-related events via the CategoryEventPublisher
        categoryEventPublisher.publishCategoryCreatedEvent(savedCategory);

        // Map to response DTO
        return categoryMapper.toDTO(savedCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public SharedCategoryResponse getCategoryById(UUID categoryId) {
        DomainEntityCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ApplicationExceptionCategory("Category not found.", HttpStatus.NOT_FOUND));

        // Map to response DTO including hierarchical relationships and tags
        return categoryMapper.toDTO(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SharedCategoryResponse> getCategories(SharedCategoryFilterCriteria filter) {
        List<DomainEntityCategory> categories = categoryRepository.findAll(filter);
        // Build hierarchical structure if necessary
        for (DomainEntityCategory category : categories) {
            buildHierarchy(category);
        }
        return categoryMapper.toDTOList(categories);
    }

    @Override
    public SharedCategoryResponse updateCategory(UUID categoryId, @Valid SharedUpdateCategoryRequest request) {
        DomainEntityCategory existingCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ApplicationExceptionCategory("Category not found.", HttpStatus.NOT_FOUND));

        // Ensure tags are unique within the category
        if (request.getTags() != null && !areTagsUnique(request.getTags())) {
            throw new ApplicationExceptionCategory("Tags must be unique within the category.", HttpStatus.BAD_REQUEST);
        }

        // Validate metadata
        validateMetadata(request.getMetadata());

        // Validate and set new parent category
        if (request.getParentCategoryId() != null) {
            if (request.getParentCategoryId().equals(categoryId)) {
                throw new ApplicationExceptionCategory("A category cannot be its own parent.", HttpStatus.BAD_REQUEST);
            }
            DomainEntityCategory newParentCategory = categoryRepository.findById(request.getParentCategoryId())
                    .orElseThrow(() -> new ApplicationExceptionCategory("Parent category not found.", HttpStatus.NOT_FOUND));
            // Prevent cyclical relationships
            if (isDescendant(existingCategory, newParentCategory.getId())) {
                throw new ApplicationExceptionCategory("Cyclical category relationship detected.", HttpStatus.BAD_REQUEST);
            }
            // Check hierarchy depth
            int depth = getHierarchyDepth(newParentCategory);
            if (depth >= MAX_HIERARCHY_DEPTH) {
                throw new ApplicationExceptionCategory("Category hierarchy depth cannot exceed " + MAX_HIERARCHY_DEPTH + ".", HttpStatus.BAD_REQUEST);
            }
            existingCategory.setParentCategory(newParentCategory);
        }

        // Map updated data
        categoryMapper.toDomain(request, existingCategory);
        existingCategory.setUpdatedAt(Instant.now());

        // Use transactions to ensure atomicity of operations and implement optimistic locking
        DomainEntityCategory updatedCategory;
        try {
            updatedCategory = categoryRepository.save(existingCategory);
        } catch (OptimisticLockingFailureException e) {
            throw new ApplicationExceptionCategory("Conflict occurred while updating the category. Please retry.", HttpStatus.CONFLICT);
        }

        // Publish event
        categoryEventPublisher.publishCategoryUpdatedEvent(updatedCategory);

        // Map to response DTO
        return categoryMapper.toDTO(updatedCategory);
    }

    @Override
    public void deleteCategory(UUID categoryId, boolean cascade) {
        DomainEntityCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ApplicationExceptionCategory("Category not found.", HttpStatus.NOT_FOUND));

        if (cascade) {
            // Delete all subcategories recursively
            deleteSubcategories(category);
        } else {
            // Check if category has subcategories
            if (category.getSubcategories() != null && !category.getSubcategories().isEmpty()) {
                throw new ApplicationExceptionCategory("Cannot delete category with subcategories unless cascade is true.", HttpStatus.BAD_REQUEST);
            }
        }

        // Delete category
        try {
            categoryRepository.delete(category);
        } catch (OptimisticLockingFailureException e) {
            throw new ApplicationExceptionCategory("Conflict occurred while deleting the category. Please retry.", HttpStatus.CONFLICT);
        }

        // Publish event
        categoryEventPublisher.publishCategoryDeletedEvent(categoryId);
    }

    private void deleteSubcategories(DomainEntityCategory category) {
        if (category.getSubcategories() != null) {
            for (DomainEntityCategory subcategory : category.getSubcategories()) {
                deleteSubcategories(subcategory);
                categoryRepository.delete(subcategory);
            }
        }
    }

    private boolean isDescendant(DomainEntityCategory category, UUID potentialAncestorId) {
        if (category.getId().equals(potentialAncestorId)) {
            return true;
        }
        DomainEntityCategory parent = category.getParentCategory();
        while (parent != null) {
            if (parent.getId().equals(potentialAncestorId)) {
                return true;
            }
            parent = parent.getParentCategory();
        }
        return false;
    }

    private int getHierarchyDepth(DomainEntityCategory category) {
        int depth = 1;
        DomainEntityCategory parent = category.getParentCategory();
        while (parent != null) {
            depth++;
            if (depth > MAX_HIERARCHY_DEPTH) {
                break;
            }
            parent = parent.getParentCategory();
        }
        return depth;
    }

    private boolean areTagsUnique(List<String> tags) {
        Set<String> tagSet = new HashSet<>(tags);
        return tagSet.size() == tags.size();
    }

    private void validateMetadata(Map<String, Object> metadata) {
        if (metadata == null) {
            return; // No metadata to validate
        }
        // Implement validation logic for metadata structure and disallowed keys or values
        if (metadata.containsKey("disallowedKey")) {
            throw new ApplicationExceptionCategory("Metadata contains disallowed keys.", HttpStatus.BAD_REQUEST);
        }
    }

    private void buildHierarchy(DomainEntityCategory category) {
        List<DomainEntityCategory> subcategories = categoryRepository.findByParentCategoryId(category.getId());
        category.setSubcategories(subcategories);
        if (subcategories != null) {
            for (DomainEntityCategory subcategory : subcategories) {
                buildHierarchy(subcategory);
            }
        }
    }
}