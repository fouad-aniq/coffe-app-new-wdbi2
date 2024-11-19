package ai.shreds.application.ports;

import ai.shreds.shared.SharedCategoryResponse;
import ai.shreds.shared.SharedCreateCategoryRequest;
import ai.shreds.shared.SharedUpdateCategoryRequest;
import ai.shreds.shared.SharedCategoryFilterCriteria;

import java.util.List;
import java.util.UUID;

public interface ApplicationCategoryInputPort {
    
    SharedCategoryResponse createCategory(SharedCreateCategoryRequest request);
    
    List<SharedCategoryResponse> getCategories(SharedCategoryFilterCriteria filter);
    
    SharedCategoryResponse getCategoryById(UUID categoryId);
    
    SharedCategoryResponse updateCategory(UUID categoryId, SharedUpdateCategoryRequest request);
    
    void deleteCategory(UUID categoryId, boolean cascade);
}
