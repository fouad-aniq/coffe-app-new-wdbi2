package ai.shreds.domain.ports;

import ai.shreds.domain.entities.DomainEntityCategory;
import ai.shreds.shared.SharedCategoryFilterCriteria;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DomainPortCategoryRepository {

    DomainEntityCategory save(DomainEntityCategory category);

    Optional<DomainEntityCategory> findById(UUID categoryId);

    List<DomainEntityCategory> findAll(SharedCategoryFilterCriteria filter);

    List<DomainEntityCategory> findByParentCategoryId(UUID parentCategoryId);

    List<DomainEntityCategory> findByTagsIn(List<String> tags);

    void deleteById(UUID categoryId);

    void delete(DomainEntityCategory category);

    boolean existsById(UUID categoryId);
}
