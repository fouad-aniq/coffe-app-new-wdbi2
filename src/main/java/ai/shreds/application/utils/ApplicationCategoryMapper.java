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
import java.util.HashMap;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import java.util.Collections;

/**
 * Mapper class for converting between domain entities and shared DTOs.
 */
@Component
public class ApplicationCategoryMapper {

    /**
     * Converts a SharedCreateCategoryRequest to a DomainEntityCategory.
     * Utilizes Lombok's builder pattern for cleaner code.
     *
     * @param request the SharedCreateCategoryRequest containing category data
     * @return a new DomainEntityCategory instance
     */
    public DomainEntityCategory toDomain(SharedCreateCategoryRequest request) {
        // Initialize builder for DomainEntityCategory
        DomainEntityCategory.DomainEntityCategoryBuilder categoryBuilder = DomainEntityCategory.builder()
                .id(UUID.randomUUID())
                .name(request.getName())
                .description(request.getDescription())
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .updatedAt(new Timestamp(System.currentTimeMillis()));

        // Set tags if provided
        if (request.getTags() != null) {
            categoryBuilder.tags(new ArrayList<>(request.getTags()));
        } else {
            categoryBuilder.tags(new ArrayList<>());
        }

        // Set metadata if provided
        if (request.getMetadata() != null) {
            categoryBuilder.metadata(new HashMap<>(request.getMetadata()));
        } else {
            categoryBuilder.metadata(new HashMap<>());
        }

        DomainEntityCategory category = categoryBuilder.build();

        // Set parent category if provided
        if (request.getParentCategoryId() != null) {
            DomainEntityCategory parentCategory = new DomainEntityCategory();
            parentCategory.setId(request.getParentCategoryId());
            category.setParentCategory(parentCategory);
        }

        return category;
    }

    /**
     * Updates an existing DomainEntityCategory with data from a SharedUpdateCategoryRequest.
     * Only updates fields that are not null in the request.
     *
     * @param request the SharedUpdateCategoryRequest containing updated data
     * @param category the existing DomainEntityCategory to update
     * @return the updated DomainEntityCategory
     */
    public DomainEntityCategory toDomain(SharedUpdateCategoryRequest request, DomainEntityCategory category) {
        // Update name if provided
        if (request.getName() != null) {
            category.setName(request.getName());
        }

        // Update description if provided
        if (request.getDescription() != null) {
            category.setDescription(request.getDescription());
        }

        // Update parent category if provided
        if (request.getParentCategoryId() != null) {
            DomainEntityCategory parentCategory = new DomainEntityCategory();
            parentCategory.setId(request.getParentCategoryId());
            category.setParentCategory(parentCategory);
        }

        // Update tags if provided
        if (request.getTags() != null) {
            category.setTags(new ArrayList<>(request.getTags()));
        }

        // Update metadata if provided
        if (request.getMetadata() != null) {
            category.setMetadata(new HashMap<>(request.getMetadata()));
        }

        // Update the updatedAt timestamp
        category.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

        return category;
    }

    /**
     * Converts a DomainEntityCategory to a SharedCategoryResponse.
     * Recursively maps subcategories to handle hierarchy.
     *
     * @param category the DomainEntityCategory to convert
     * @return a SharedCategoryResponse representing the category
     */
    public SharedCategoryResponse toDTO(DomainEntityCategory category) {
        SharedCategoryResponse response = new SharedCategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setDescription(category.getDescription());

        // Set parent category ID if parent exists
        if (category.getParentCategory() != null) {
            response.setParentCategoryId(category.getParentCategory().getId());
        }

        response.setTags(category.getTags());
        response.setMetadata(category.getMetadata());
        response.setCreatedAt(category.getCreatedAt());
        response.setUpdatedAt(category.getUpdatedAt());

        // Recursively map subcategories
        if (category.getSubcategories() != null && !category.getSubcategories().isEmpty()) {
            response.setSubcategories(
                category.getSubcategories().stream()
                    .map(this::toDTO)
                    .collect(Collectors.toList())
            );
        } else {
            response.setSubcategories(Collections.emptyList());
        }

        return response;
    }

    /**
     * Converts a list of DomainEntityCategory instances to a list of SharedCategoryResponse instances.
     * Handles null or empty lists by returning an empty list.
     *
     * @param categories the list of DomainEntityCategory instances
     * @return a list of SharedCategoryResponse instances
     */
    public List<SharedCategoryResponse> toDTOList(List<DomainEntityCategory> categories) {
        if (categories == null || categories.isEmpty()) {
            return Collections.emptyList();
        }

        return categories.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}