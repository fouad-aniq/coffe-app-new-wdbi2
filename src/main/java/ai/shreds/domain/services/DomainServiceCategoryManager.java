package ai.shreds.domain.services;

import ai.shreds.domain.entities.DomainEntityCategory;
import ai.shreds.domain.exceptions.DomainExceptionCategory;
import ai.shreds.domain.ports.DomainPortCategoryEvent;
import ai.shreds.domain.ports.DomainPortCategoryRepository;
import ai.shreds.shared.SharedCategoryFilterCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class DomainServiceCategoryManager {

    private static final int MAX_HIERARCHY_DEPTH = 10;
    private final DomainPortCategoryRepository categoryRepository;
    private final DomainPortCategoryEvent categoryEventPublisher;

    @Autowired
    public DomainServiceCategoryManager(DomainPortCategoryRepository categoryRepository,
                                        DomainPortCategoryEvent categoryEventPublisher) {
        this.categoryRepository = categoryRepository;
        this.categoryEventPublisher = categoryEventPublisher;
    }

    @Transactional
    public DomainEntityCategory createCategory(DomainEntityCategory category) throws DomainExceptionCategory {
        // Validate mandatory fields
        validateMandatoryFields(category);

        // Validate tags are unique within the category
        validateUniqueTags(category.getTags());

        // Validate metadata
        validateMetadata(category.getMetadata());

        // Check parent category
        DomainEntityCategory parentCategory = null;
        if (category.getParentCategory() != null) {
            UUID parentId = category.getParentCategory().getId();
            parentCategory = categoryRepository.findById(parentId)
                    .orElseThrow(() -> new DomainExceptionCategory("Parent category not found", "PARENT_CATEGORY_NOT_FOUND"));
            // Prevent cyclical relationships
            if (parentId.equals(category.getId())) {
                throw new DomainExceptionCategory("Category cannot be its own parent", "CYCLICAL_RELATIONSHIP");
            }
            if (isDescendant(category.getId(), parentId)) {
                throw new DomainExceptionCategory("Cyclical hierarchy detected", "CYCLICAL_RELATIONSHIP");
            }
            category.setParentCategory(parentCategory);
        }

        // Ensure unique name under same parent
        ensureUniqueNameUnderParent(category, parentCategory);

        // Limit category hierarchy depth
        int depth = getCategoryDepth(category);
        if (depth > MAX_HIERARCHY_DEPTH) {
            throw new DomainExceptionCategory("Category hierarchy depth exceeds the limit", "HIERARCHY_DEPTH_EXCEEDED");
        }

        // Set timestamps
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        category.setCreatedAt(currentTime);
        category.setUpdatedAt(currentTime);

        // Save category
        DomainEntityCategory savedCategory = categoryRepository.save(category);

        // Publish event
        categoryEventPublisher.publishCategoryCreatedEvent(savedCategory);

        return savedCategory;
    }

    @Transactional
    public DomainEntityCategory updateCategory(UUID categoryId, DomainEntityCategory updatedData) throws DomainExceptionCategory {
        // Retrieve existing category
        DomainEntityCategory existingCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new DomainExceptionCategory("Category not found", "CATEGORY_NOT_FOUND"));

        // Validate mandatory fields
        validateMandatoryFields(updatedData);

        // Validate tags are unique within the category
        validateUniqueTags(updatedData.getTags());

        // Validate metadata
        validateMetadata(updatedData.getMetadata());

        // Handle parent category change
        if (updatedData.getParentCategory() != null) {
            UUID newParentId = updatedData.getParentCategory().getId();
            if (newParentId.equals(categoryId)) {
                throw new DomainExceptionCategory("Category cannot be its own parent", "CYCLICAL_RELATIONSHIP");
            }
            if (isDescendant(categoryId, newParentId)) {
                throw new DomainExceptionCategory("Cyclical hierarchy detected", "CYCLICAL_RELATIONSHIP");
            }
            DomainEntityCategory newParentCategory = categoryRepository.findById(newParentId)
                    .orElseThrow(() -> new DomainExceptionCategory("Parent category not found", "PARENT_CATEGORY_NOT_FOUND"));
            existingCategory.setParentCategory(newParentCategory);
        }

        // Update fields
        existingCategory.setName(updatedData.getName());
        existingCategory.setDescription(updatedData.getDescription());
        existingCategory.setMetadata(updatedData.getMetadata());
        existingCategory.setTags(updatedData.getTags());

        // Ensure unique name under same parent
        ensureUniqueNameUnderParent(existingCategory, existingCategory.getParentCategory());

        // Limit category hierarchy depth
        int depth = getCategoryDepth(existingCategory);
        if (depth > MAX_HIERARCHY_DEPTH) {
            throw new DomainExceptionCategory("Category hierarchy depth exceeds the limit", "HIERARCHY_DEPTH_EXCEEDED");
        }

        // Update timestamp
        existingCategory.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

        // Save the updated category
        DomainEntityCategory updatedCategory = categoryRepository.save(existingCategory);

        // Publish event
        categoryEventPublisher.publishCategoryUpdatedEvent(updatedCategory);

        return updatedCategory;
    }

    @Transactional
    public void deleteCategory(UUID categoryId, boolean cascade) throws DomainExceptionCategory {
        // Retrieve category
        DomainEntityCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new DomainExceptionCategory("Category not found", "CATEGORY_NOT_FOUND"));

        if (cascade) {
            deleteCategoryRecursively(category);
        } else {
            List<DomainEntityCategory> subcategories = categoryRepository.findByParentCategoryId(categoryId);
            if (!subcategories.isEmpty()) {
                throw new DomainExceptionCategory("Cannot delete category with subcategories without cascade delete", "CATEGORY_HAS_SUBCATEGORIES");
            }
            categoryRepository.delete(category);

            // Publish event
            categoryEventPublisher.publishCategoryDeletedEvent(categoryId);
        }
    }

    public DomainEntityCategory getCategoryById(UUID categoryId) throws DomainExceptionCategory {
        // Retrieve category
        DomainEntityCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new DomainExceptionCategory("Category not found", "CATEGORY_NOT_FOUND"));

        // Assemble hierarchical structure
        category.setSubcategories(getSubcategoriesRecursively(category));

        return category;
    }

    public List<DomainEntityCategory> getAllCategories(SharedCategoryFilterCriteria filter) {
        // Retrieve categories based on filter criteria
        List<DomainEntityCategory> categories = categoryRepository.findAll(filter);

        // Assemble hierarchical structures
        for (DomainEntityCategory category : categories) {
            category.setSubcategories(getSubcategoriesRecursively(category));
        }

        return categories;
    }

    private boolean isDescendant(UUID categoryId, UUID potentialAncestorId) {
        if (categoryId.equals(potentialAncestorId)) {
            return true;
        }
        List<DomainEntityCategory> subcategories = categoryRepository.findByParentCategoryId(potentialAncestorId);
        for (DomainEntityCategory subcategory : subcategories) {
            if (isDescendant(categoryId, subcategory.getId())) {
                return true;
            }
        }
        return false;
    }

    private int getCategoryDepth(DomainEntityCategory category) {
        int depth = 0;
        DomainEntityCategory currentCategory = category;
        while (currentCategory.getParentCategory() != null) {
            depth++;
            currentCategory = currentCategory.getParentCategory();
            if (depth > MAX_HIERARCHY_DEPTH) {
                break;
            }
        }
        return depth;
    }

    private void deleteCategoryRecursively(DomainEntityCategory category) {
        List<DomainEntityCategory> subcategories = categoryRepository.findByParentCategoryId(category.getId());
        for (DomainEntityCategory subcategory : subcategories) {
            deleteCategoryRecursively(subcategory);
        }
        categoryRepository.delete(category);

        // Publish deletion event
        categoryEventPublisher.publishCategoryDeletedEvent(category.getId());
    }

    private List<DomainEntityCategory> getSubcategoriesRecursively(DomainEntityCategory parentCategory) {
        List<DomainEntityCategory> subcategories = categoryRepository.findByParentCategoryId(parentCategory.getId());
        for (DomainEntityCategory subcategory : subcategories) {
            subcategory.setSubcategories(getSubcategoriesRecursively(subcategory));
        }
        return subcategories;
    }

    // Validate mandatory fields
    private void validateMandatoryFields(DomainEntityCategory category) throws DomainExceptionCategory {
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            throw new DomainExceptionCategory("Category name is mandatory", "CATEGORY_NAME_MANDATORY");
        }
    }

    // Validate that tags are unique within the category
    private void validateUniqueTags(List<String> tags) throws DomainExceptionCategory {
        if (tags != null) {
            Set<String> tagSet = new HashSet<>(tags);
            if (tagSet.size() != tags.size()) {
                throw new DomainExceptionCategory("Tags must be unique within the category", "DUPLICATE_TAGS");
            }
        }
    }

    // Validate metadata to ensure it conforms to expected JSON structure
    private void validateMetadata(Map<String, Object> metadata) throws DomainExceptionCategory {
        if (metadata != null) {
            // Implement metadata validation logic here
            // For example, check for disallowed keys or values
            Set<String> allowedKeys = Set.of("allowedKey1", "allowedKey2");
            for (String key : metadata.keySet()) {
                if (!allowedKeys.contains(key)) {
                    throw new DomainExceptionCategory("Metadata contains disallowed key: " + key, "INVALID_METADATA_KEY");
                }
            }
        }
    }

    // Ensure unique name under the same parent
    private void ensureUniqueNameUnderParent(DomainEntityCategory category, DomainEntityCategory parentCategory) throws DomainExceptionCategory {
        UUID parentIdValue = parentCategory != null ? parentCategory.getId() : null;
        List<DomainEntityCategory> siblingCategories = categoryRepository.findByParentCategoryId(parentIdValue);
        for (DomainEntityCategory sibling : siblingCategories) {
            if (!sibling.getId().equals(category.getId()) && sibling.getName().equals(category.getName())) {
                throw new DomainExceptionCategory("Category name must be unique under the same parent", "DUPLICATE_CATEGORY_NAME");
            }
        }
    }
}
