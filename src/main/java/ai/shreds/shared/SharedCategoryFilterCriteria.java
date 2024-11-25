package ai.shreds.shared;

import java.util.UUID;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SharedCategoryFilterCriteria {

    private UUID parentId;

    // Limit the maximum number of tags to 50
    @Size(max = 50)
    private List<String> tags;

    // Page number should be at least 0
    @Min(0)
    @Builder.Default
    private Integer page = 0;

    // Page size should be at least 1
    @Min(1)
    @Builder.Default
    private Integer size = 10;

}