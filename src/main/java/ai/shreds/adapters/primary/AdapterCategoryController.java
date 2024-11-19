package ai.shreds.adapters.primary;

import ai.shreds.adapters.exceptions.AdapterExceptionCategory;
import ai.shreds.application.ports.ApplicationCategoryInputPort;
import ai.shreds.shared.SharedCategoryFilterCriteria;
import ai.shreds.shared.SharedCategoryResponse;
import ai.shreds.shared.SharedCreateCategoryRequest;
import ai.shreds.shared.SharedUpdateCategoryRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/categories")
public class AdapterCategoryController {

    private final ApplicationCategoryInputPort applicationCategoryInputPort;

    @Autowired
    public AdapterCategoryController(ApplicationCategoryInputPort applicationCategoryInputPort) {
        this.applicationCategoryInputPort = applicationCategoryInputPort;
    }

    @PostMapping
    public SharedCategoryResponse createCategory(@RequestBody SharedCreateCategoryRequest request) {
        try {
            return applicationCategoryInputPort.createCategory(request);
        } catch (AdapterExceptionCategory ex) {
            throw ex;
        } catch (Exception ex) {
            throw new AdapterExceptionCategory("Failed to create category: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public List<SharedCategoryResponse> getCategories(@ModelAttribute SharedCategoryFilterCriteria filter) {
        try {
            return applicationCategoryInputPort.getCategories(filter);
        } catch (AdapterExceptionCategory ex) {
            throw ex;
        } catch (Exception ex) {
            throw new AdapterExceptionCategory("Failed to retrieve categories: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public SharedCategoryResponse getCategoryById(@PathVariable UUID id) {
        try {
            return applicationCategoryInputPort.getCategoryById(id);
        } catch (AdapterExceptionCategory ex) {
            throw ex;
        } catch (Exception ex) {
            throw new AdapterExceptionCategory("Failed to retrieve category: " + ex.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public SharedCategoryResponse updateCategory(@PathVariable UUID id, @RequestBody SharedUpdateCategoryRequest request) {
        try {
            return applicationCategoryInputPort.updateCategory(id, request);
        } catch (AdapterExceptionCategory ex) {
            throw ex;
        } catch (Exception ex) {
            throw new AdapterExceptionCategory("Failed to update category: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable UUID id, @RequestParam(defaultValue = "false") boolean cascade) {
        try {
            applicationCategoryInputPort.deleteCategory(id, cascade);
        } catch (AdapterExceptionCategory ex) {
            throw ex;
        } catch (Exception ex) {
            throw new AdapterExceptionCategory("Failed to delete category: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}