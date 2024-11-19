package ai.shreds.shared;

import java.util.UUID;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import javax.validation.constraints.Min;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SharedCategoryFilterCriteria {

    private UUID parentId;
    private List<String> tags;

    @Min(0)
    @Builder.Default
    private Integer page = 0;

    @Min(1)
    @Builder.Default
    private Integer size = 10;

}
