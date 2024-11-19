package ai.shreds.domain.services;

import ai.shreds.domain.entities.DomainEntityCategory;
import ai.shreds.domain.exceptions.DomainExceptionCategory;
import ai.shreds.domain.ports.DomainPortCategoryEvent;
import ai.shreds.domain.ports.DomainPortCategoryRepository;
import ai.shreds.shared.SharedCategoryFilterCriteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

@Service
public class DomainServiceCategoryManager {

    private static final int MAX_HIERARCHY_DEPTH = 10;
    private final DomainPortCategoryRepository categoryRepository;
    private final DomainPortCategoryEvent categoryEventPublisher;

    public DomainServiceCategoryManager(DomainPortCategoryRepository categoryRepository,
                                        DomainPortCategoryEvent categoryEventPublisher) {
        this.categoryRepository = categoryRepository;
        this.categoryEventPublisher = categoryEventPublisher;
    }

    @Transactional
    public DomainEntityCategory createCategory(DomainEntityCategory category) {
        validateMandatoryFields(category);
        validateUniqueTags(category.getTags());
        validateMetadata(category.getMetadata());

        DomainEntityCategory parentCategory = null;
        if (category.getParentCategory() != null) {
            UUID parentId = category.getParentCategory().getId();
            parentCategory = categoryRepository.findById(parentId)
                    .orElseThrow(() -> new DomainExceptionCategory("Parent category not found", "PARENT_CATEGORY_NOT_FOUND"));
            if (parentId.equals(category.getId())) {
                throw new DomainExceptionCategory("Category cannot be its own parent", "CYCLICAL_RELATIONSHIP");
            }
            if (isDescendant(parentId, category.getId())) {
                throw new DomainExceptionCategory("Cyclical hierarchy detected", "CYCLICAL_RELATIONSHIP");
            }
            category.setParentCategory(parentCategory);
        }

        ensureUniqueNameUnderParent(category, parentCategory);

        int depth = getCategoryDepth(category);
        if (depth > MAX_HIERARCHY_DEPTH) {
            throw new DomainExceptionCategory("Category hierarchy depth exceeds the limit", "HIERARCHY_DEPTH_EXCEEDED");
        }

        Instant currentTime = Instant.now();
        category.setCreatedAt(Timestamp.from(currentTime));
        category.setUpdatedAt(Timestamp.from(currentTime));

        DomainEntityCategory savedCategory = categoryRepository.save(category);

        categoryEventPublisher.publishCategoryCreatedEvent(savedCategory);

        return savedCategory;
    }

    @Transactional
    public DomainEntityCategory updateCategory(UUID categoryId, DomainEntityCategory updatedData) {
        DomainEntityCategory existingCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new DomainExceptionCategory("Category not found", "CATEGORY_NOT_FOUND"));

        validateMandatoryFields(updatedData);
        validateUniqueTags(updatedData.getTags());
        validateMetadata(updatedData.getMetadata());

        if (updatedData.getParentCategory() != null) {
            UUID newParentId = updatedData.getParentCategory().getId();
            if (newParentId.equals(categoryId)) {
                throw new DomainExceptionCategory("Category cannot be its own parent", "CYCLICAL_RELATIONSHIP");
            }
            if (isDescendant(newParentId, categoryId)) {
                throw new DomainExceptionCategory("Cyclical hierarchy detected", "CYCLICAL_RELATIONSHIP");
            }
            DomainEntityCategory newParentCategory = categoryRepository.findById(newParentId)
                    .orElseThrow(() -> new DomainExceptionCategory("Parent category not found", "PARENT_CATEGORY_NOT_FOUND"));
            existingCategory.setParentCategory(newParentCategory);
        }

        existingCategory.setName(updatedData.getName());
        existingCategory.setDescription(updatedData.getDescription());
        existingCategory.setMetadata(updatedData.getMetadata());
        existingCategory.setTags(updatedData.getTags());

        ensureUniqueNameUnderParent(existingCategory, existingCategory.getParentCategory());

        int depth = getCategoryDepth(existingCategory);
        if (depth > MAX_HIERARCHY_DEPTH) {
            throw new DomainExceptionCategory("Category hierarchy depth exceeds the limit", "HIERARCHY_DEPTH_EXCEEDED");
        }

        existingCategory.setUpdatedAt(Timestamp.from(Instant.now()));

        DomainEntityCategory updatedCategory = categoryRepository.save(existingCategory);

        categoryEventPublisher.publishCategoryUpdatedEvent(updatedCategory);

        return updatedCategory;
    }

    @Transactional
    public void deleteCategory(UUID categoryId, boolean cascade) {
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

            categoryEventPublisher.publishCategoryDeletedEvent(categoryId);
        }
    }

    public DomainEntityCategory getCategoryById(UUID categoryId) {
        DomainEntityCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new DomainExceptionCategory("Category not found", "CATEGORY_NOT_FOUND"));

        category.setSubcategories(getSubcategoriesRecursively(category));

        return category;
    }

    public List<DomainEntityCategory> getAllCategories(SharedCategoryFilterCriteria filter) {
        List<DomainEntityCategory> categories = categoryRepository.findAll(filter);

        for (DomainEntityCategory category : categories) {
            category.setSubcategories(getSubcategoriesRecursively(category));
        }

        return categories;
    }

    private boolean isDescendant(UUID ancestorId, UUID descendantId) {
        if (ancestorId.equals(descendantId)) {
            return true;
        }
        List<DomainEntityCategory> subcategories = categoryRepository.findByParentCategoryId(ancestorId);
        for (DomainEntityCategory subcategory : subcategories) {
            if (isDescendant(subcategory.getId(), descendantId)) {
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

        categoryEventPublisher.publishCategoryDeletedEvent(category.getId());
    }

    private List<DomainEntityCategory> getSubcategoriesRecursively(DomainEntityCategory parentCategory) {
        List<DomainEntityCategory> subcategories = categoryRepository.findByParentCategoryId(parentCategory.getId());
        for (DomainEntityCategory subcategory : subcategories) {
            subcategory.setSubcategories(getSubcategoriesRecursively(subcategory));
        }
        return subcategories;
    }

    private void validateMandatoryFields(DomainEntityCategory category) {
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            throw new DomainExceptionCategory("Category name is mandatory", "CATEGORY_NAME_MANDATORY");
        }
    }

    private void validateUniqueTags(List<String> tags) {
        if (tags != null) {
            Set<String> tagSet = new HashSet<>(tags);
            if (tagSet.size() != tags.size()) {
                throw new DomainExceptionCategory("Tags must be unique within the category", "DUPLICATE_TAGS");
            }
        }
    }

    private void validateMetadata(Map<String, Object> metadata) {
        if (metadata != null) {
            Set<String> allowedKeys = new HashSet<>(Arrays.asList("allowedKey1", "allowedKey2"));
            for (String key : metadata.keySet()) {
                if (!allowedKeys.contains(key)) {
                    throw new DomainExceptionCategory("Metadata contains disallowed key: " + key, "INVALID_METADATA_KEY");
                }
            }
        }
    }

    private void ensureUniqueNameUnderParent(DomainEntityCategory category, DomainEntityCategory parentCategory) {
        UUID parentIdValue = (parentCategory != null) ? parentCategory.getId() : null;
        List<DomainEntityCategory> siblingCategories = categoryRepository.findByParentCategoryId(parentIdValue);
        for (DomainEntityCategory sibling : siblingCategories) {
            if (!sibling.getId().equals(category.getId()) && sibling.getName().equals(category.getName())) {
                throw new DomainExceptionCategory("Category name must be unique under the same parent", "DUPLICATE_CATEGORY_NAME");
            }
        }
    }
}