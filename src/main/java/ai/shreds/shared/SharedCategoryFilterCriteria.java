package shared;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.UUID;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SharedCategoryFilterCriteria {
    private UUID parentId;
    private List<String> tags;
    private Integer page;
    private Integer size;
}