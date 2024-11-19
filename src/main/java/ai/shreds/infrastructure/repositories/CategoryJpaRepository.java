package ai.shreds.infrastructure.repositories;

import ai.shreds.domain.entities.DomainEntityCategory;
import ai.shreds.domain.ports.DomainPortCategoryRepository;
import ai.shreds.infrastructure.entities.InfrastructureCategoryEntity;
import ai.shreds.infrastructure.exceptions.InfrastructureExceptionCategory;
import ai.shreds.infrastructure.utils.InfrastructureCategoryEntityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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
    public DomainEntityCategory save(DomainEntityCategory category) throws InfrastructureExceptionCategory {
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
    public Optional<DomainEntityCategory> findById(UUID categoryId) throws InfrastructureExceptionCategory {
        try {
            Optional<InfrastructureCategoryEntity> entityOpt = jpaRepository.findById(categoryId);
            return entityOpt.map(mapper::toDomain);
        } catch (DataAccessException e) {
            throw new InfrastructureExceptionCategory("Error finding category by ID", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<DomainEntityCategory> findAll() throws InfrastructureExceptionCategory {
        try {
            List<InfrastructureCategoryEntity> entities = jpaRepository.findAll();
            return entities.stream().map(mapper::toDomain).collect(Collectors.toList());
        } catch (DataAccessException e) {
            throw new InfrastructureExceptionCategory("Error finding all categories", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<DomainEntityCategory> findByParentCategoryId(UUID parentCategoryId) throws InfrastructureExceptionCategory {
        try {
            List<InfrastructureCategoryEntity> entities = jpaRepository.findByParentCategoryId(parentCategoryId);
            return entities.stream().map(mapper::toDomain).collect(Collectors.toList());
        } catch (DataAccessException e) {
            throw new InfrastructureExceptionCategory("Error finding categories by parent ID", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<DomainEntityCategory> findByTagsIn(List<String> tags) throws InfrastructureExceptionCategory {
        try {
            List<InfrastructureCategoryEntity> entities = jpaRepository.findByTagsIn(tags);
            return entities.stream().map(mapper::toDomain).collect(Collectors.toList());
        } catch (DataAccessException e) {
            throw new InfrastructureExceptionCategory("Error finding categories by tags", e);
        }
    }

    @Override
    @Transactional
    public void deleteById(UUID categoryId) throws InfrastructureExceptionCategory {
        try {
            jpaRepository.deleteById(categoryId);
        } catch (DataAccessException e) {
            throw new InfrastructureExceptionCategory("Error deleting category by ID", e);
        }
    }

    @Override
    @Transactional
    public void delete(DomainEntityCategory category) throws InfrastructureExceptionCategory {
        try {
            InfrastructureCategoryEntity entity = mapper.toInfrastructure(category);
            jpaRepository.delete(entity);
        } catch (DataAccessException e) {
            throw new InfrastructureExceptionCategory("Error deleting category", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(UUID categoryId) throws InfrastructureExceptionCategory {
        try {
            return jpaRepository.existsById(categoryId);
        } catch (DataAccessException e) {
            throw new InfrastructureExceptionCategory("Error checking existence of category by ID", e);
        }
    }

    // Additional methods for pagination and filtering can be added here
}
