package ai.shreds.infrastructure.repositories;

import ai.shreds.infrastructure.entities.InfrastructureCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.UUID;

// Added @Repository annotation for clarity and to indicate that this interface is a repository component
@Repository
public interface JpaCategoryRepository extends JpaRepository<InfrastructureCategoryEntity, UUID>, JpaSpecificationExecutor<InfrastructureCategoryEntity> {

    // Existing method to find categories by parent category ID
    List<InfrastructureCategoryEntity> findByParentCategoryId(UUID parentCategoryId);

    // Existing method to find categories by tags
    List<InfrastructureCategoryEntity> findByTagsIn(List<String> tags);

    // Added method with custom query to retrieve subcategories using CTE for recursive queries
    @Query(value = "WITH RECURSIVE subcategories AS (" +
           " SELECT * FROM categories WHERE id = :parentId " +
           " UNION ALL " +
           " SELECT c.* FROM categories c " +
           " INNER JOIN subcategories s ON c.parent_category_id = s.id " +
           ") SELECT * FROM subcategories", nativeQuery = true)
    List<InfrastructureCategoryEntity> findSubcategoriesUsingCTE(@Param("parentId") UUID parentCategoryId);

    // Added method with custom JPQL query to find all categories filtered by parent ID and tags, with pagination support
    @Query("SELECT c FROM InfrastructureCategoryEntity c WHERE " +
           "(:parentId IS NULL OR c.parentCategory.id = :parentId) AND " +
           "(:tags IS NULL OR c.tags IN :tags)")
    List<InfrastructureCategoryEntity> findAllByFilterCriteria(@Param("parentId") UUID parentId,
                                                               @Param("tags") List<String> tags,
                                                               Pageable pageable);
}