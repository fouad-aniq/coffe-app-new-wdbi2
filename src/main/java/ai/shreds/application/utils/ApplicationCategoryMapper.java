package ai.shreds.application.utils;

import ai.shreds.shared.SharedCreateCategoryRequest;
import ai.shreds.shared.SharedUpdateCategoryRequest;
import ai.shreds.shared.SharedCategoryResponse;
import ai.shreds.domain.entities.DomainEntityCategory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
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

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "parentCategory", expression = "java(parentCategoryFromId(request.getParentCategoryId()))"),
        @Mapping(target = "createdAt", expression = "java(java.sql.Timestamp.from(java.time.Instant.now()))"),
        @Mapping(target = "updatedAt", expression = "java(java.sql.Timestamp.from(java.time.Instant.now()))"),
        @Mapping(target = "subcategories", ignore = true)
    })
    DomainEntityCategory toDomain(SharedCreateCategoryRequest request);

    @Mappings({
        @Mapping(target = "id", source = "category.id"),
        @Mapping(target = "parentCategory", expression = "java(parentCategoryFromId(request.getParentCategoryId()))"),
        @Mapping(target = "createdAt", source = "category.createdAt"),
        @Mapping(target = "updatedAt", expression = "java(java.sql.Timestamp.from(java.time.Instant.now()))"),
        @Mapping(target = "subcategories", source = "category.subcategories")
    })
    DomainEntityCategory toDomain(SharedUpdateCategoryRequest request, @MappingTarget DomainEntityCategory category);

    @Mappings({
        @Mapping(target = "parentCategoryId", source = "parentCategory.id"),
        @Mapping(target = "subcategories", qualifiedByName = "toDTOList")
    })
    SharedCategoryResponse toDTO(DomainEntityCategory category);

    @Named("toDTOList")
    List<SharedCategoryResponse> toDTOList(List<DomainEntityCategory> categories);

    default DomainEntityCategory parentCategoryFromId(UUID parentCategoryId) {
        if (parentCategoryId == null) {
            return null;
        }
        DomainEntityCategory parentCategory = new DomainEntityCategory();
        parentCategory.setId(parentCategoryId);
        return parentCategory;
    }
}