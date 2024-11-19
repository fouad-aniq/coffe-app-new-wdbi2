package ai.shreds.application.utils;

import ai.shreds.shared.SharedCreateCategoryRequest;
import ai.shreds.shared.SharedUpdateCategoryRequest;
import ai.shreds.shared.SharedCategoryResponse;
import ai.shreds.domain.entities.DomainEntityCategory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Mapper
public interface ApplicationCategoryMapper {

    ApplicationCategoryMapper INSTANCE = Mappers.getMapper(ApplicationCategoryMapper.class);

    // Mapping method for creating a new DomainEntityCategory from SharedCreateCategoryRequest
    @Mapping(target = "id", ignore = true) // ID will be generated, so ignore it in mapping
    @Mapping(target = "parentCategory", expression = "java(parentCategoryFromId(request.getParentCategoryId()))") // Map parentCategory using helper method
    @Mapping(target = "createdAt", expression = "java(currentTimestamp())") // Set createdAt to current timestamp
    @Mapping(target = "updatedAt", expression = "java(currentTimestamp())") // Set updatedAt to current timestamp
    @Mapping(target = "subcategories", ignore = true) // Ignore subcategories during creation
    DomainEntityCategory toDomain(SharedCreateCategoryRequest request);

    // Mapping method for updating an existing DomainEntityCategory with SharedUpdateCategoryRequest
    @Mapping(target = "id", source = "category.id") // Preserve existing ID
    @Mapping(target = "parentCategory", expression = "java(parentCategoryFromId(request.getParentCategoryId()))")
    @Mapping(target = "createdAt", source = "category.createdAt") // Preserve original createdAt timestamp
    @Mapping(target = "updatedAt", expression = "java(currentTimestamp())") // Update updatedAt to current timestamp
    @Mapping(target = "subcategories", source = "category.subcategories") // Preserve existing subcategories
    DomainEntityCategory toDomain(SharedUpdateCategoryRequest request, @MappingTarget DomainEntityCategory category);

    // Mapping method to convert DomainEntityCategory to SharedCategoryResponse
    @Mapping(target = "parentCategoryId", source = "parentCategory.id") // Map parentCategoryId from parentCategory's ID
    @Mapping(target = "subcategories", qualifiedByName = "toDTOList") // Map subcategories using toDTOList method
    SharedCategoryResponse toDTO(DomainEntityCategory category);

    @Named("toDTOList")
    List<SharedCategoryResponse> toDTOList(List<DomainEntityCategory> categories);

    // Helper method to create a DomainEntityCategory from a parentCategoryId
    default DomainEntityCategory parentCategoryFromId(UUID parentCategoryId) {
        if (parentCategoryId == null) {
            return null;
        }
        DomainEntityCategory parentCategory = new DomainEntityCategory();
        parentCategory.setId(parentCategoryId);
        return parentCategory;
    }

    // Helper method to get the current timestamp
    default Timestamp currentTimestamp() {
        return Timestamp.from(Instant.now());
    }
}