package ai.shreds.infrastructure.repositories;

import ai.shreds.infrastructure.entities.InfrastructureCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaCategoryRepository extends JpaRepository<InfrastructureCategoryEntity, UUID> {

    List<InfrastructureCategoryEntity> findByParentCategoryId(UUID parentCategoryId);

    List<InfrastructureCategoryEntity> findByTagsIn(List<String> tags);

}