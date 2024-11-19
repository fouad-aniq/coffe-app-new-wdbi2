package ai.shreds.shared;

import lombok.Data;
import java.util.UUID;
import java.util.List;

@Data
public class SharedCategoryFilterCriteria {
    private UUID parentId;
    private List<String> tags;
    private Integer page;
    private Integer size;

    public SharedCategoryFilterCriteria() {
        // No-argument constructor
    }

    public SharedCategoryFilterCriteria(UUID parentId, List<String> tags, Integer page, Integer size) {
        this.parentId = parentId;
        this.tags = tags;
        this.page = page;
        this.size = size;
    }
}