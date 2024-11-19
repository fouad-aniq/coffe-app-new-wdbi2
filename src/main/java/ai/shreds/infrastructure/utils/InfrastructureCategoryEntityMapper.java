package ai.shreds.infrastructure.utils;

import ai.shreds.domain.entities.DomainEntityCategory;
import ai.shreds.infrastructure.entities.InfrastructureCategoryEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InfrastructureCategoryEntityMapper {

    DomainEntityCategory toDomain(InfrastructureCategoryEntity entity);

    InfrastructureCategoryEntity toInfrastructure(DomainEntityCategory domain);
}
