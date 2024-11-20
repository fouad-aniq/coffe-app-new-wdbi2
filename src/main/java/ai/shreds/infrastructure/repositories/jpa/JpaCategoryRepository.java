package ai.shreds.infrastructure.repositories.jpa;

import ai.shreds.infrastructure.entities.InfrastructureCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaCategoryRepository extends JpaRepository<InfrastructureCategoryEntity, UUID>, JpaSpecificationExecutor<InfrastructureCategoryEntity> {

    /**
     * Finds categories by their parent category ID.
     *
     * @param parentCategoryId the UUID of the parent category
     * @return a list of categories that have the specified parent category
     */
    List<InfrastructureCategoryEntity> findByParentCategoryId(UUID parentCategoryId);

    /**
     * Finds categories associated with any of the specified tags.
     *
     * @param tags a list of tags
     * @return a list of categories that have any of the specified tags
     */
    List<InfrastructureCategoryEntity> findByTagsIn(List<String> tags);
}