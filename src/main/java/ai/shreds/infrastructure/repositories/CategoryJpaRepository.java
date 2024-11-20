package ai.shreds.infrastructure.repositories;

import ai.shreds.domain.entities.DomainEntityCategory;
import ai.shreds.domain.ports.DomainPortCategoryRepository;
import ai.shreds.infrastructure.entities.InfrastructureCategoryEntity;
import ai.shreds.infrastructure.exceptions.InfrastructureExceptionCategory;
import ai.shreds.infrastructure.utils.InfrastructureCategoryEntityMapper;
import ai.shreds.shared.SharedCategoryFilterCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
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
import java.util.stream.Collectors;

@Repository
public class CategoryJpaRepository implements DomainPortCategoryRepository {

    @Autowired
    private JpaCategoryRepository jpaRepository;

    @Autowired
    private InfrastructureCategoryEntityMapper mapper;

    @Override
    @Transactional
    public DomainEntityCategory save(DomainEntityCategory category) {
        try {
            InfrastructureCategoryEntity entity = mapper.toInfrastructure(category);
            InfrastructureCategoryEntity savedEntity = jpaRepository.save(entity);
            return mapper.toDomain(savedEntity);
        } catch (DataAccessException e) {
            throw new InfrastructureExceptionCategory("Error saving category", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DomainEntityCategory> findById(UUID categoryId) {
        try {
            Optional<InfrastructureCategoryEntity> entityOpt = jpaRepository.findById(categoryId);
            return entityOpt.map(mapper::toDomain);
        } catch (DataAccessException e) {
            throw new InfrastructureExceptionCategory("Error finding category by ID", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<DomainEntityCategory> findAll(SharedCategoryFilterCriteria filter) {
        try {
            Specification<InfrastructureCategoryEntity> specification = buildSpecification(filter);
            Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize());
            Page<InfrastructureCategoryEntity> page = jpaRepository.findAll(specification, pageable);
            return page.getContent().stream().map(mapper::toDomain).collect(Collectors.toList());
        } catch (DataAccessException e) {
            throw new InfrastructureExceptionCategory("Error finding all categories", e);
        }
    }

    private Specification<InfrastructureCategoryEntity> buildSpecification(SharedCategoryFilterCriteria filter) {
        return (Root<InfrastructureCategoryEntity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (filter.getParentId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("parentCategory").get("id"), filter.getParentId()));
            }
            if (filter.getTags() != null && !filter.getTags().isEmpty()) {
                predicates.add(criteriaBuilder.function("array_overlap", Boolean.class, root.get("tags"), criteriaBuilder.literal(filter.getTags())));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    @Override
    @Transactional(readOnly = true)
    public List<DomainEntityCategory> findByParentCategoryId(UUID parentCategoryId) {
        try {
            List<InfrastructureCategoryEntity> entities = jpaRepository.findByParentCategoryId(parentCategoryId);
            return entities.stream().map(mapper::toDomain).collect(Collectors.toList());
        } catch (DataAccessException e) {
            throw new InfrastructureExceptionCategory("Error finding categories by parent ID", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<DomainEntityCategory> findByTagsIn(List<String> tags) {
        try {
            List<InfrastructureCategoryEntity> entities = jpaRepository.findByTagsIn(tags);
            return entities.stream().map(mapper::toDomain).collect(Collectors.toList());
        } catch (DataAccessException e) {
            throw new InfrastructureExceptionCategory("Error finding categories by tags", e);
        }
    }

    @Override
    @Transactional
    public void deleteById(UUID categoryId) {
        try {
            jpaRepository.deleteById(categoryId);
        } catch (DataAccessException e) {
            throw new InfrastructureExceptionCategory("Error deleting category by ID", e);
        }
    }

    @Override
    @Transactional
    public void delete(DomainEntityCategory category) {
        try {
            InfrastructureCategoryEntity entity = mapper.toInfrastructure(category);
            jpaRepository.delete(entity);
        } catch (DataAccessException e) {
            throw new InfrastructureExceptionCategory("Error deleting category", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(UUID categoryId) {
        try {
            return jpaRepository.existsById(categoryId);
        } catch (DataAccessException e) {
            throw new InfrastructureExceptionCategory("Error checking existence of category by ID", e);
        }
    }
}