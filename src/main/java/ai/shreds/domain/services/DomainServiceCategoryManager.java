package ai.shreds.domain.services;

import ai.shreds.domain.entities.DomainEntityCategory;
import ai.shreds.domain.exceptions.DomainExceptionCategory;
import ai.shreds.domain.ports.DomainPortCategoryEvent;
import ai.shreds.domain.ports.DomainPortCategoryRepository;
import ai.shreds.shared.SharedCategoryFilterCriteria;

import java.util.*;
import java.util.UUID;

public class DomainServiceCategoryManager {

    private final DomainPortCategoryRepository categoryRepository;
    private final DomainPortCategoryEvent categoryEventPublisher;
    private static final int MAX_HIERARCHY_DEPTH = 10;

    public DomainServiceCategoryManager(DomainPortCategoryRepository categoryRepository,
                                        DomainPortCategoryEvent categoryEventPublisher) {
        this.categoryRepository = categoryRepository;
        this.categoryEventPublisher = categoryEventPublisher;
    }

    public DomainEntityCategory createCategory(DomainEntityCategory category) throws DomainExceptionCategory {
        validateCategory(category);
        // Validate parent category
        if (category.getParentCategory() != null) {
            UUID parentCategoryId = category.getParentCategory().getId();
            DomainEntityCategory parentCategory = categoryRepository.findById(parentCategoryId)
                    .orElseThrow(() -> new DomainExceptionCategory("Parent category not found", "PARENT_CATEGORY_NOT_FOUND"));
            // Ensure the category is not its own parent
            if (parentCategory.getId().equals(category.getId())) {
                throw new DomainExceptionCategory("Category cannot be its own parent", "INVALID_PARENT_CATEGORY");
            }
            // Prevent cyclical hierarchy
            if (isDescendant(parentCategory, category)) {
                throw new DomainExceptionCategory("Cyclical hierarchy detected", "CYCLICAL_HIERARCHY_DETECTED");
            }
            // Check hierarchy depth
            int depth = getHierarchyDepth(parentCategory);
            if (depth >= MAX_HIERARCHY_DEPTH) {
                throw new DomainExceptionCategory("Maximum hierarchy depth exceeded", "MAX_HIERARCHY_DEPTH_EXCEEDED");
            }
            category.setParentCategory(parentCategory);
        }
        // Ensure unique name under the same parent
        boolean nameExists = categoryRepository.existsByNameAndParentCategoryId(category.getName(),
                category.getParentCategory() != null ? category.getParentCategory().getId() : null);
        if (nameExists) {
            throw new DomainExceptionCategory("Category name must be unique under the same parent", "DUPLICATE_CATEGORY_NAME");
        }
        // Save category
        DomainEntityCategory savedCategory = categoryRepository.save(category);
        // Publish event
        categoryEventPublisher.publishCategoryCreatedEvent(savedCategory);
        return savedCategory;
    }

    public DomainEntityCategory updateCategory(UUID categoryId, DomainEntityCategory updatedData) throws DomainExceptionCategory {
        // Retrieve existing category
        DomainEntityCategory existingCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new DomainExceptionCategory("Category not found", "CATEGORY_NOT_FOUND"));
        // Update fields
        existingCategory.setName(updatedData.getName());
        existingCategory.setDescription(updatedData.getDescription());
        existingCategory.setTags(updatedData.getTags());
        existingCategory.setMetadata(updatedData.getMetadata());
        // Validate updated category
        validateCategory(existingCategory);
        // Validate and set parent category
        if (updatedData.getParentCategory() != null) {
            UUID parentCategoryId = updatedData.getParentCategory().getId();
            DomainEntityCategory parentCategory = categoryRepository.findById(parentCategoryId)
                    .orElseThrow(() -> new DomainExceptionCategory("Parent category not found", "PARENT_CATEGORY_NOT_FOUND"));
            // Ensure the category is not its own parent
            if (parentCategory.getId().equals(categoryId)) {
                throw new DomainExceptionCategory("Category cannot be its own parent", "INVALID_PARENT_CATEGORY");
            }
            // Prevent cyclical hierarchy
            if (isDescendant(parentCategory, existingCategory)) {
                throw new DomainExceptionCategory("Cyclical hierarchy detected", "CYCLICAL_HIERARCHY_DETECTED");
            }
            // Check hierarchy depth
            int depth = getHierarchyDepth(parentCategory);
            if (depth >= MAX_HIERARCHY_DEPTH) {
                throw new DomainExceptionCategory("Maximum hierarchy depth exceeded", "MAX_HIERARCHY_DEPTH_EXCEEDED");
            }
            existingCategory.setParentCategory(parentCategory);
        } else {
            existingCategory.setParentCategory(null);
        }
        // Ensure unique name under the same parent
        boolean nameExists = categoryRepository.existsByNameAndParentCategoryIdExceptId(existingCategory.getName(),
                existingCategory.getParentCategory() != null ? existingCategory.getParentCategory().getId() : null,
                categoryId);
        if (nameExists) {
            throw new DomainExceptionCategory("Category name must be unique under the same parent", "DUPLICATE_CATEGORY_NAME");
        }
        // Save updated category
        DomainEntityCategory savedCategory = categoryRepository.save(existingCategory);
        // Publish event
        categoryEventPublisher.publishCategoryUpdatedEvent(savedCategory);
        return savedCategory;
    }

    public void deleteCategory(UUID categoryId, boolean cascade) throws DomainExceptionCategory {
        // Retrieve category
        DomainEntityCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new DomainExceptionCategory("Category not found", "CATEGORY_NOT_FOUND"));
        if (cascade) {
            // Delete all subcategories recursively
            deleteSubcategories(category);
        } else {
            // Check if category has subcategories
            List<DomainEntityCategory> subcategories = categoryRepository.findByParentCategoryId(categoryId);
            if (!subcategories.isEmpty()) {
                throw new DomainExceptionCategory("Category has subcategories and cannot be deleted without cascade", "CATEGORY_HAS_SUBCATEGORIES");
            }
        }
        // Delete category
        categoryRepository.delete(category);
        // Publish event
        categoryEventPublisher.publishCategoryDeletedEvent(categoryId);
    }

    public DomainEntityCategory getCategoryById(UUID categoryId) throws DomainExceptionCategory {
        // Retrieve category
        DomainEntityCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new DomainExceptionCategory("Category not found", "CATEGORY_NOT_FOUND"));
        // Assemble hierarchical structure
        category.setSubcategories(getSubcategories(category));
        return category;
    }

    public List<DomainEntityCategory> getAllCategories(SharedCategoryFilterCriteria filter) {
        // Retrieve categories
        List<DomainEntityCategory> categories = categoryRepository.findAll(filter);
        // Assemble hierarchical structures
        for (DomainEntityCategory category : categories) {
            category.setSubcategories(getSubcategories(category));
        }
        return categories;
    }

    private void validateCategory(DomainEntityCategory category) throws DomainExceptionCategory {
        // Validate name
        if (category.getName() == null || category.getName().isEmpty()) {
            throw new DomainExceptionCategory("Category name is mandatory", "CATEGORY_NAME_MISSING");
        }
        // Ensure tags are unique
        if (category.getTags() != null) {
            Set<String> uniqueTags = new HashSet<>(category.getTags());
            if (uniqueTags.size() != category.getTags().size()) {
                throw new DomainExceptionCategory("Tags must be unique within the category", "DUPLICATE_TAGS");
            }
        }
        // Validate metadata
        validateMetadata(category.getMetadata());
    }

    private void validateMetadata(Map<String, Object> metadata) throws DomainExceptionCategory {
        // Implement metadata validation logic
        if (metadata != null) {
            // Example validation: disallow certain keys
            if (metadata.containsKey("disallowedKey")) {
                throw new DomainExceptionCategory("Metadata contains disallowed keys", "INVALID_METADATA");
            }
            // Additional validation rules can be added here
        }
    }

    private int getHierarchyDepth(DomainEntityCategory category) {
        int depth = 0;
        DomainEntityCategory current = category;
        while (current.getParentCategory() != null) {
            depth++;
            current = current.getParentCategory();
            if (depth > MAX_HIERARCHY_DEPTH) {
                break;
            }
        }
        return depth;
    }

    private boolean isDescendant(DomainEntityCategory descendant, DomainEntityCategory ancestor) {
        if (descendant.getParentCategory() == null) {
            return false;
        }
        if (descendant.getParentCategory().getId().equals(ancestor.getId())) {
            return true;
        }
        return isDescendant(descendant.getParentCategory(), ancestor);
    }

    private void deleteSubcategories(DomainEntityCategory category) {
        List<DomainEntityCategory> subcategories = categoryRepository.findByParentCategoryId(category.getId());
        for (DomainEntityCategory subcategory : subcategories) {
            deleteSubcategories(subcategory);
            categoryRepository.delete(subcategory);
        }
    }

    private List<DomainEntityCategory> getSubcategories(DomainEntityCategory category) {
        List<DomainEntityCategory> subcategories = categoryRepository.findByParentCategoryId(category.getId());
        for (DomainEntityCategory subcategory : subcategories) {
            subcategory.setSubcategories(getSubcategories(subcategory));
        }
        return subcategories;
    }
}
