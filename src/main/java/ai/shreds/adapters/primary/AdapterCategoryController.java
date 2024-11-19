package ai.shreds.adapters.primary;

import ai.shreds.application.ports.ApplicationCategoryInputPort;
import ai.shreds.shared.SharedCreateCategoryRequest;
import ai.shreds.shared.SharedUpdateCategoryRequest;
import ai.shreds.shared.SharedCategoryResponse;
import ai.shreds.shared.SharedCategoryFilterCriteria;
import ai.shreds.adapters.exceptions.AdapterExceptionCategory;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseStatus;

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
    @ResponseStatus(HttpStatus.CREATED)
    public SharedCategoryResponse createCategory(@Valid @RequestBody SharedCreateCategoryRequest request) {
        try {
            return applicationCategoryInputPort.createCategory(request);
        } catch (Exception e) {
            throw new AdapterExceptionCategory("Error creating category", HttpStatus.INTERNAL_SERVER_ERROR, e);
        }
    }

    @GetMapping
    public List<SharedCategoryResponse> getCategories(@ModelAttribute SharedCategoryFilterCriteria filter) {
        try {
            return applicationCategoryInputPort.getCategories(filter);
        } catch (Exception e) {
            throw new AdapterExceptionCategory("Error retrieving categories", HttpStatus.INTERNAL_SERVER_ERROR, e);
        }
    }

    @GetMapping("/{id}")
    public SharedCategoryResponse getCategoryById(@PathVariable UUID id) {
        try {
            return applicationCategoryInputPort.getCategoryById(id);
        } catch (Exception e) {
            throw new AdapterExceptionCategory("Category not found", HttpStatus.NOT_FOUND, e);
        }
    }

    @PutMapping("/{id}")
    public SharedCategoryResponse updateCategory(@PathVariable UUID id,
                                                 @Valid @RequestBody SharedUpdateCategoryRequest request) {
        try {
            return applicationCategoryInputPort.updateCategory(id, request);
        } catch (Exception e) {
            throw new AdapterExceptionCategory("Error updating category", HttpStatus.INTERNAL_SERVER_ERROR, e);
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable UUID id,
                               @RequestParam(defaultValue = "false") boolean cascade) {
        try {
            applicationCategoryInputPort.deleteCategory(id, cascade);
        } catch (Exception e) {
            throw new AdapterExceptionCategory("Error deleting category", HttpStatus.INTERNAL_SERVER_ERROR, e);
        }
    }
}
