package ai.shreds.shared;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Singular;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.UniqueElements;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import ai.shreds.shared.validation.ValidCategoryParent;
import ai.shreds.shared.validation.ValidMetadata;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SharedCategoryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private UUID id;

    @NotNull(message = "The 'name' field is required and must not be null when creating a category.")
    private String name;

    private String description;

    @ValidCategoryParent
    private UUID parentCategoryId;

    @Singular
    @Valid
    @Size(max = 100, message = "Tags list can have at most 100 items.")
    @UniqueElements(message = "Tags must be unique within the category.")
    private List<@NotEmpty(message = "Tags must be non-empty strings.") String> tags;

    @ValidMetadata
    private Map<String, Object> metadata;

    private Timestamp createdAt;

    private Timestamp updatedAt;

}