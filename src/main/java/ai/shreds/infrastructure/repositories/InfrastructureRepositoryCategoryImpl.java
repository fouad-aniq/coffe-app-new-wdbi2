package ai.shreds.infrastructure.repositories;

import ai.shreds.domain.entities.DomainEntityCategory;
import ai.shreds.domain.ports.DomainPortCategoryRepository;
import ai.shreds.infrastructure.entities.InfrastructureCategoryEntity;
import ai.shreds.infrastructure.exceptions.InfrastructureExceptionCategory;
import ai.shreds.infrastructure.utils.InfrastructureCategoryEntityMapper;
import ai.shreds.shared.SharedCategoryFilterCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class InfrastructureRepositoryCategoryImpl implements DomainPortCategoryRepository {

    private final CategoryJpaRepository categoryJpaRepository;
    private final InfrastructureCategoryEntityMapper categoryEntityMapper;

    @Autowired
    public InfrastructureRepositoryCategoryImpl(CategoryJpaRepository categoryJpaRepository,
                                                InfrastructureCategoryEntityMapper categoryEntityMapper) {
        this.categoryJpaRepository = categoryJpaRepository;
        this.categoryEntityMapper = categoryEntityMapper;
    }

    @Override
    @Transactional
    public DomainEntityCategory save(DomainEntityCategory category) {
        try {
            InfrastructureCategoryEntity entity = categoryEntityMapper.toInfrastructure(category);
            InfrastructureCategoryEntity savedEntity = categoryJpaRepository.save(entity);
            return categoryEntityMapper.toDomain(savedEntity);
        } catch (OptimisticLockingFailureException e) {
            throw new InfrastructureExceptionCategory("Failed to save category due to concurrent update", e);
        } catch (DataIntegrityViolationException e) {
            throw new InfrastructureExceptionCategory("Data integrity violation while saving category", e);
        } catch (Exception e) {
            throw new InfrastructureExceptionCategory("An error occurred while saving the category", e);
        }
    }

    @Override
    public Optional<DomainEntityCategory> findById(UUID categoryId) {
        Optional<InfrastructureCategoryEntity> entityOptional = categoryJpaRepository.findById(categoryId);
        return entityOptional.map(categoryEntityMapper::toDomain);
    }

    @Override
    public List<DomainEntityCategory> findAll(SharedCategoryFilterCriteria filter) {
        Specification<InfrastructureCategoryEntity> spec = buildSpecification(filter);
        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize());
        List<InfrastructureCategoryEntity> entities = categoryJpaRepository.findAll(spec, pageable).getContent();
        return categoryEntityMapper.toDomainList(entities);
    }

    @Override
    public List<DomainEntityCategory> findByParentCategoryId(UUID parentCategoryId) {
        // Uses optimized query to retrieve subcategories efficiently.
        List<InfrastructureCategoryEntity> entities = categoryJpaRepository.findByParentCategoryId(parentCategoryId);
        return categoryEntityMapper.toDomainList(entities);
    }

    @Override
    public List<DomainEntityCategory> findByTagsIn(List<String> tags) {
        List<InfrastructureCategoryEntity> entities = categoryJpaRepository.findByTagsIn(tags);
        return categoryEntityMapper.toDomainList(entities);
    }

    @Override
    @Transactional
    public void deleteById(UUID categoryId) {
        try {
            categoryJpaRepository.deleteById(categoryId);
        } catch (DataIntegrityViolationException e) {
            throw new InfrastructureExceptionCategory("Data integrity violation while deleting category by ID", e);
        } catch (Exception e) {
            throw new InfrastructureExceptionCategory("An error occurred while deleting the category", e);
        }
    }

    @Override
    @Transactional
    public void delete(DomainEntityCategory category) {
        try {
            InfrastructureCategoryEntity entity = categoryEntityMapper.toInfrastructure(category);
            categoryJpaRepository.delete(entity);
        } catch (DataIntegrityViolationException e) {
            throw new InfrastructureExceptionCategory("Data integrity violation while deleting category", e);
        } catch (Exception e) {
            throw new InfrastructureExceptionCategory("An error occurred while deleting the category", e);
        }
    }

    @Override
    public boolean existsById(UUID categoryId) {
        return categoryJpaRepository.existsById(categoryId);
    }

    private Specification<InfrastructureCategoryEntity> buildSpecification(SharedCategoryFilterCriteria filter) {
        return (Root<InfrastructureCategoryEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (filter.getParentId() != null) {
                // Leveraging PostgreSQL Common Table Expressions (CTEs) if necessary for recursive queries.
                predicates.add(cb.equal(root.get("parentCategory").get("id"), filter.getParentId()));
            }
            if (filter.getTags() != null && !filter.getTags().isEmpty()) {
                predicates.add(root.get("tags").in(filter.getTags()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
