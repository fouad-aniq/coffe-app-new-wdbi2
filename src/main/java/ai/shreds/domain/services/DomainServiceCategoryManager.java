\npackage ai.shreds.domain.services;

import ai.shreds.domain.entities.DomainEntityCategory;
import ai.shreds.domain.exceptions.DomainExceptionCategory;
import ai.shreds.domain.ports.DomainPortCategoryEvent;
import ai.shreds.domain.ports.DomainPortCategoryRepository;
import ai.shreds.shared.SharedCategoryFilterCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DomainServiceCategoryManager {

    private final DomainPortCategoryRepository categoryRepository;
    private final DomainPortCategoryEvent categoryEventPublisher;

    public DomainEntityCategory createCategory(DomainEntityCategory category) throws DomainExceptionCategory {
        category.validate();
        if (category.getParentCategory() != null) {
            UUID parentCategoryId = category.getParentCategory().getId();
            DomainEntityCategory parentCategory = categoryRepository.findById(parentCategoryId)
                    .orElseThrow(() -> new DomainExceptionCategory(\"Parent category not found\", \"PARENT_CATEGORY_NOT_FOUND\"));
            category.setParentCategory(parentCategory);
        }
        boolean nameExists = categoryRepository.existsByNameAndParentCategoryId(category.getName(),
                category.getParentCategory() != null ? category.getParentCategory().getId() : null);
        if (nameExists) {
            throw new DomainExceptionCategory(\"Category name must be unique under the same parent\", \"DUPLICATE_CATEGORY_NAME\");
        }
        DomainEntityCategory savedCategory = categoryRepository.save(category);
        categoryEventPublisher.publishCategoryCreatedEvent(savedCategory);
        return savedCategory;
    }

    public DomainEntityCategory updateCategory(UUID categoryId, DomainEntityCategory updatedData) throws DomainExceptionCategory {
        DomainEntityCategory existingCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new DomainExceptionCategory(\"Category not found\", \"CATEGORY_NOT_FOUND\"));
        existingCategory.setName(updatedData.getName());
        existingCategory.setDescription(updatedData.getDescription());
        existingCategory.setTags(updatedData.getTags());
        existingCategory.setMetadata(updatedData.getMetadata());
        existingCategory.validate();
        if (updatedData.getParentCategory() != null) {
            UUID parentCategoryId = updatedData.getParentCategory().getId();
            DomainEntityCategory parentCategory = categoryRepository.findById(parentCategoryId)
                    .orElseThrow(() -> new DomainExceptionCategory(\"Parent category not found\", \"PARENT_CATEGORY_NOT_FOUND\"));
            existingCategory.setParentCategory(parentCategory);
        } else {
            existingCategory.setParentCategory(null);
        }
        boolean nameExists = categoryRepository.existsByNameAndParentCategoryIdExceptId(existingCategory.getName(),
                existingCategory.getParentCategory() != null ? existingCategory.getParentCategory().getId() : null,
                categoryId);
        if (nameExists) {
            throw new DomainExceptionCategory(\"Category name must be unique under the same parent\", \"DUPLICATE_CATEGORY_NAME\");
        }
        DomainEntityCategory savedCategory = categoryRepository.save(existingCategory);
        categoryEventPublisher.publishCategoryUpdatedEvent(savedCategory);
        return savedCategory;
    }

    public void deleteCategory(UUID categoryId, boolean cascade) throws DomainExceptionCategory {
        DomainEntityCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new DomainExceptionCategory(\"Category not found\", \"CATEGORY_NOT_FOUND\"));
        if (cascade) {
            deleteSubcategories(category);
        } else {
            List<DomainEntityCategory> subcategories = categoryRepository.findByParentCategoryId(categoryId);
            if (!subcategories.isEmpty()) {
                throw new DomainExceptionCategory(\"Category has subcategories and cannot be deleted without cascade\", \"CATEGORY_HAS_SUBCATEGORIES\");
            }
        }
        categoryRepository.delete(category);
        categoryEventPublisher.publishCategoryDeletedEvent(categoryId);
    }

    public DomainEntityCategory getCategoryById(UUID categoryId) throws DomainExceptionCategory {
        DomainEntityCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new DomainExceptionCategory(\"Category not found\", \"CATEGORY_NOT_FOUND\"));
        category.setSubcategories(getSubcategories(category));
        return category;
    }

    public List<DomainEntityCategory> getAllCategories(SharedCategoryFilterCriteria filter) {
        List<DomainEntityCategory> categories = categoryRepository.findAll(filter);
        for (DomainEntityCategory category : categories) {
            category.setSubcategories(getSubcategories(category));
        }
        return categories;
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

    /**
     * Validates if the provided parent category ID is valid for the given category.
     * Ensures that a category cannot be its own parent and that the parent category exists.
     *
     * @param categoryId       The ID of the category being validated. Can be null when creating a new category.
     * @param parentCategoryId The ID of the proposed parent category.
     * @return true if the parent category is valid, false otherwise.
     */
    public boolean isValidParentCategory(UUID categoryId, UUID parentCategoryId) {
        if (categoryId != null && categoryId.equals(parentCategoryId)) {
            return false; // A category cannot be its own parent
        }
        // Additional logic to check for cyclical relationships can be added here
        return categoryRepository.existsById(parentCategoryId);
    }
}