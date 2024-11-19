package ai.shreds.adapters.primary;

import ai.shreds.adapters.exceptions.AdapterExceptionCategory;
import ai.shreds.application.exceptions.ApplicationExceptionCategory;
import ai.shreds.application.ports.ApplicationCategoryInputPort;
import ai.shreds.shared.SharedCategoryFilterCriteria;
import ai.shreds.shared.SharedCategoryResponse;
import ai.shreds.shared.SharedCreateCategoryRequest;
import ai.shreds.shared.SharedUpdateCategoryRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class AdapterCategoryController {

    private final ApplicationCategoryInputPort applicationCategoryInputPort;

    @PostMapping
    public SharedCategoryResponse createCategory(@Valid @RequestBody SharedCreateCategoryRequest request) {
        try {
            return applicationCategoryInputPort.createCategory(request);
        } catch (ApplicationExceptionCategory e) {
            throw new AdapterExceptionCategory(e.getMessage(), e.getStatus());
        }
    }

    @GetMapping
    public List<SharedCategoryResponse> getCategories(@ModelAttribute SharedCategoryFilterCriteria filter) {
        try {
            return applicationCategoryInputPort.getCategories(filter);
        } catch (ApplicationExceptionCategory e) {
            throw new AdapterExceptionCategory(e.getMessage(), e.getStatus());
        }
    }

    @GetMapping("/{id}")
    public SharedCategoryResponse getCategoryById(@PathVariable UUID id) {
        try {
            return applicationCategoryInputPort.getCategoryById(id);
        } catch (ApplicationExceptionCategory e) {
            throw new AdapterExceptionCategory(e.getMessage(), e.getStatus());
        }
    }

    @PutMapping("/{id}")
    public SharedCategoryResponse updateCategory(@PathVariable UUID id, @Valid @RequestBody SharedUpdateCategoryRequest request) {
        try {
            return applicationCategoryInputPort.updateCategory(id, request);
        } catch (ApplicationExceptionCategory e) {
            throw new AdapterExceptionCategory(e.getMessage(), e.getStatus());
        }
    }

    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable UUID id, @RequestParam(defaultValue = "false") boolean cascade) {
        try {
            applicationCategoryInputPort.deleteCategory(id, cascade);
        } catch (ApplicationExceptionCategory e) {
            throw new AdapterExceptionCategory(e.getMessage(), e.getStatus());
        }
    }
}