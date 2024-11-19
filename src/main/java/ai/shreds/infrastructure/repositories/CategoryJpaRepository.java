package ai.shreds.infrastructure.repositories;

import ai.shreds.infrastructure.entities.InfrastructureCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository; // Added import for Repository annotation

import java.util.List;
import java.util.UUID;

@Repository // Added Repository annotation to indicate this is a Spring Data repository
public interface CategoryJpaRepository extends JpaRepository<InfrastructureCategoryEntity, UUID>, JpaSpecificationExecutor<InfrastructureCategoryEntity> {
    List<InfrastructureCategoryEntity> findByParentCategoryId(UUID parentCategoryId);
    List<InfrastructureCategoryEntity> findByTagsIn(List<String> tags);
}