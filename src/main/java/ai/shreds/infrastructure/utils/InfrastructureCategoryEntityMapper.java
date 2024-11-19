package ai.shreds.infrastructure.utils;

import ai.shreds.domain.entities.DomainEntityCategory;
import ai.shreds.infrastructure.entities.InfrastructureCategoryEntity;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.*;

/**
 * Mapper to convert between InfrastructureCategoryEntity and DomainEntityCategory.
 */
@Mapper(componentModel = "spring")
public interface InfrastructureCategoryEntityMapper {

    /**
     * Maps InfrastructureCategoryEntity to DomainEntityCategory.
     * @param entity the InfrastructureCategoryEntity to map
     * @param context the context to avoid cyclic mappings
     * @return the mapped DomainEntityCategory
     */
    DomainEntityCategory toDomain(InfrastructureCategoryEntity entity, @Context CycleAvoidingMappingContext context);

    /**
     * Maps DomainEntityCategory to InfrastructureCategoryEntity.
     * @param domain the DomainEntityCategory to map
     * @param context the context to avoid cyclic mappings
     * @return the mapped InfrastructureCategoryEntity
     */
    InfrastructureCategoryEntity toInfrastructure(DomainEntityCategory domain, @Context CycleAvoidingMappingContext context);

    @Named("setToList")
    default List<String> setToList(Set<String> set) {
        return new ArrayList<>(set);
    }

    @Named("listToSet")
    default Set<String> listToSet(List<String> list) {
        return new HashSet<>(list);
    }

    /**
     * Custom mapping method for parentCategory to prevent infinite recursion.
     */
    default DomainEntityCategory mapParentToDomain(InfrastructureCategoryEntity entity, @Context CycleAvoidingMappingContext context) {
        return entity == null ? null : toDomain(entity, context);
    }

    /**
     * Custom mapping method for parentCategory to prevent infinite recursion.
     */
    default InfrastructureCategoryEntity mapParentToInfrastructure(DomainEntityCategory domain, @Context CycleAvoidingMappingContext context) {
        return domain == null ? null : toInfrastructure(domain, context);
    }

    /**
     * Custom mapping method for subcategories to prevent infinite recursion.
     */
    default List<DomainEntityCategory> mapSubcategoriesToDomain(List<InfrastructureCategoryEntity> entities, @Context CycleAvoidingMappingContext context) {
        if (entities == null) return null;
        List<DomainEntityCategory> list = new ArrayList<>();
        for (InfrastructureCategoryEntity entity : entities) {
            list.add(toDomain(entity, context));
        }
        return list;
    }

    /**
     * Custom mapping method for subcategories to prevent infinite recursion.
     */
    default List<InfrastructureCategoryEntity> mapSubcategoriesToInfrastructure(List<DomainEntityCategory> domains, @Context CycleAvoidingMappingContext context) {
        if (domains == null) return null;
        List<InfrastructureCategoryEntity> list = new ArrayList<>();
        for (DomainEntityCategory domain : domains) {
            list.add(toInfrastructure(domain, context));
        }
        return list;
    }
}