package ai.shreds.adapters.primary;

import ai.shreds.application.ports.ApplicationCategoryInputPort;
import ai.shreds.shared.SharedCreateCategoryRequest;
import ai.shreds.shared.SharedUpdateCategoryRequest;
import ai.shreds.shared.SharedCategoryResponse;
import ai.shreds.shared.SharedCategoryFilterCriteria;
import ai.shreds.adapters.exceptions.AdapterExceptionCategory;
import ai.shreds.application.exceptions.ApplicationExceptionCategory;

import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class AdapterCategoryController {

    private final ApplicationCategoryInputPort applicationCategoryInputPort;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SharedCategoryResponse createCategory(@Valid @RequestBody SharedCreateCategoryRequest request) {
        try {
            return applicationCategoryInputPort.createCategory(request);
        } catch (ApplicationExceptionCategory e) {
            throw new AdapterExceptionCategory(e.getMessage(), e.getStatus(), e);
        } catch (Exception e) {
            throw new AdapterExceptionCategory("Error creating category", HttpStatus.INTERNAL_SERVER_ERROR, e);
        }
    }

    @GetMapping
    public List<SharedCategoryResponse> getCategories(@ModelAttribute SharedCategoryFilterCriteria filter) {
        try {
            return applicationCategoryInputPort.getCategories(filter);
        } catch (ApplicationExceptionCategory e) {
            throw new AdapterExceptionCategory(e.getMessage(), e.getStatus(), e);
        } catch (Exception e) {
            throw new AdapterExceptionCategory("Error retrieving categories", HttpStatus.INTERNAL_SERVER_ERROR, e);
        }
    }

    @GetMapping("/{id}")
    public SharedCategoryResponse getCategoryById(@PathVariable UUID id) {
        try {
            return applicationCategoryInputPort.getCategoryById(id);
        } catch (ApplicationExceptionCategory e) {
            throw new AdapterExceptionCategory(e.getMessage(), e.getStatus(), e);
        } catch (Exception e) {
            throw new AdapterExceptionCategory("Category not found", HttpStatus.NOT_FOUND, e);
        }
    }

    @PutMapping("/{id}")
    public SharedCategoryResponse updateCategory(@PathVariable UUID id,
                                                 @Valid @RequestBody SharedUpdateCategoryRequest request) {
        try {
            return applicationCategoryInputPort.updateCategory(id, request);
        } catch (ApplicationExceptionCategory e) {
            throw new AdapterExceptionCategory(e.getMessage(), e.getStatus(), e);
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
        } catch (ApplicationExceptionCategory e) {
            throw new AdapterExceptionCategory(e.getMessage(), e.getStatus(), e);
        } catch (Exception e) {
            throw new AdapterExceptionCategory("Error deleting category", HttpStatus.INTERNAL_SERVER_ERROR, e);
        }
    }
}