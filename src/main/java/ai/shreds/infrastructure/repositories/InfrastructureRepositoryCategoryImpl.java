package ai.shreds.infrastructure.repositories;

import ai.shreds.domain.entities.DomainEntityCategory;
import ai.shreds.domain.ports.DomainPortCategoryRepository;
import ai.shreds.infrastructure.entities.InfrastructureCategoryEntity;
import ai.shreds.infrastructure.exceptions.InfrastructureExceptionCategory;
import ai.shreds.infrastructure.repositories.jpa.InfrastructureCategoryEntityRepository;
import ai.shreds.infrastructure.utils.InfrastructureCategoryEntityMapper;
import ai.shreds.shared.SharedCategoryFilterCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.dao.DataAccessException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class InfrastructureRepositoryCategoryImpl implements DomainPortCategoryRepository {

    private final InfrastructureCategoryEntityRepository categoryRepository;
    private final InfrastructureCategoryEntityMapper categoryMapper;

    @Autowired
    public InfrastructureRepositoryCategoryImpl(InfrastructureCategoryEntityRepository categoryRepository,
                                                InfrastructureCategoryEntityMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @Override
    public DomainEntityCategory save(DomainEntityCategory category) {
        try {
            InfrastructureCategoryEntity entity = categoryMapper.toInfrastructure(category);
            InfrastructureCategoryEntity savedEntity = categoryRepository.save(entity);
            return categoryMapper.toDomain(savedEntity);
        } catch (DataAccessException e) {
            throw new InfrastructureExceptionCategory("Error saving category");
        }
    }

    @Override
    public Optional<DomainEntityCategory> findById(UUID categoryId) {
        try {
            Optional<InfrastructureCategoryEntity> entityOptional = categoryRepository.findById(categoryId);
            return entityOptional.map(categoryMapper::toDomain);
        } catch (DataAccessException e) {
            throw new InfrastructureExceptionCategory("Error finding category by ID");
        }
    }

    @Override
    public List<DomainEntityCategory> findAll(SharedCategoryFilterCriteria filter) {
        try {
            Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize());
            List<InfrastructureCategoryEntity> entities = categoryRepository.findAll(pageable).getContent();
            return entities.stream()
                    .map(categoryMapper::toDomain)
                    .collect(Collectors.toList());
        } catch (DataAccessException e) {
            throw new InfrastructureExceptionCategory("Error finding all categories");
        }
    }

    @Override
    public List<DomainEntityCategory> findByParentCategoryId(UUID parentCategoryId) {
        try {
            List<InfrastructureCategoryEntity> entities = categoryRepository.findByParentCategory_Id(parentCategoryId);
            return entities.stream()
                    .map(categoryMapper::toDomain)
                    .collect(Collectors.toList());
        } catch (DataAccessException e) {
            throw new InfrastructureExceptionCategory("Error finding categories by parent category ID");
        }
    }

    @Override
    public List<DomainEntityCategory> findByTagsIn(List<String> tags) {
        try {
            List<InfrastructureCategoryEntity> entities = categoryRepository.findByTagsIn(tags);
            return entities.stream()
                    .map(categoryMapper::toDomain)
                    .collect(Collectors.toList());
        } catch (DataAccessException e) {
            throw new InfrastructureExceptionCategory("Error finding categories by tags");
        }
    }

    @Override
    public void deleteById(UUID categoryId) {
        try {
            categoryRepository.deleteById(categoryId);
        } catch (DataAccessException e) {
            throw new InfrastructureExceptionCategory("Error deleting category by ID");
        }
    }

    @Override
    public void delete(DomainEntityCategory category) {
        try {
            InfrastructureCategoryEntity entity = categoryMapper.toInfrastructure(category);
            categoryRepository.delete(entity);
        } catch (DataAccessException e) {
            throw new InfrastructureExceptionCategory("Error deleting category");
        }
    }

    @Override
    public boolean existsById(UUID categoryId) {
        try {
            return categoryRepository.existsById(categoryId);
        } catch (DataAccessException e) {
            throw new InfrastructureExceptionCategory("Error checking if category exists by ID");
        }
    }
}