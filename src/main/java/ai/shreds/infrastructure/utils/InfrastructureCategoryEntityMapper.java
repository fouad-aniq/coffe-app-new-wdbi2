package ai.shreds.infrastructure.utils;

import ai.shreds.domain.entities.DomainEntityCategory;
import ai.shreds.infrastructure.entities.InfrastructureCategoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InfrastructureCategoryEntityMapper {

    @Mapping(target = "parentCategory", ignore = true)
    @Mapping(target = "subcategories", ignore = true)
    DomainEntityCategory toDomain(InfrastructureCategoryEntity entity);

    @Mapping(target = "parentCategory", ignore = true)
    @Mapping(target = "subcategories", ignore = true)
    InfrastructureCategoryEntity toInfrastructure(DomainEntityCategory domain);
}