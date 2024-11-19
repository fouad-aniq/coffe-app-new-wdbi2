package ai.shreds.application.utils;

import ai.shreds.domain.entities.DomainEntityCategory;
import ai.shreds.shared.SharedCreateCategoryRequest;
import ai.shreds.shared.SharedUpdateCategoryRequest;
import ai.shreds.shared.SharedCategoryResponse;

import java.util.List;
import java.util.UUID;
import java.util.Map;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class ApplicationCategoryMapper {

    public DomainEntityCategory toDomain(SharedCreateCategoryRequest request) {
        DomainEntityCategory category = new DomainEntityCategory();
        category.setId(UUID.randomUUID());
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        
        if (request.getParentCategoryId() != null) {
            DomainEntityCategory parentCategory = new DomainEntityCategory();
            parentCategory.setId(request.getParentCategoryId());
            category.setParentCategory(parentCategory);
        }
        
        category.setTags(request.getTags());
        category.setMetadata(request.getMetadata());
        
        Timestamp now = new Timestamp(System.currentTimeMillis());
        category.setCreatedAt(now);
        category.setUpdatedAt(now);
        
        return category;
    }

    public DomainEntityCategory toDomain(SharedUpdateCategoryRequest request, DomainEntityCategory category) {
        if (request.getName() != null) {
            category.setName(request.getName());
        }

        if (request.getDescription() != null) {
            category.setDescription(request.getDescription());
        }

        if (request.getParentCategoryId() != null) {
            DomainEntityCategory parentCategory = new DomainEntityCategory();
            parentCategory.setId(request.getParentCategoryId());
            category.setParentCategory(parentCategory);
        }

        if (request.getTags() != null) {
            category.setTags(request.getTags());
        }

        if (request.getMetadata() != null) {
            category.setMetadata(request.getMetadata());
        }

        Timestamp now = new Timestamp(System.currentTimeMillis());
        category.setUpdatedAt(now);

        return category;
    }

    public SharedCategoryResponse toDTO(DomainEntityCategory category) {
        SharedCategoryResponse response = new SharedCategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setDescription(category.getDescription());
        
        if (category.getParentCategory() != null) {
            response.setParentCategoryId(category.getParentCategory().getId());
        }
        
        response.setTags(category.getTags());
        response.setMetadata(category.getMetadata());
        response.setCreatedAt(category.getCreatedAt());
        response.setUpdatedAt(category.getUpdatedAt());
        
        if (category.getSubcategories() != null && !category.getSubcategories().isEmpty()) {
            response.setSubcategories(category.getSubcategories()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList()));
        } else {
            response.setSubcategories(new ArrayList<>());
        }
        
        return response;
    }

    public List<SharedCategoryResponse> toDTOList(List<DomainEntityCategory> categories) {
        if (categories == null) {
            return new ArrayList<>();
        }
        
        return categories.stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
}
