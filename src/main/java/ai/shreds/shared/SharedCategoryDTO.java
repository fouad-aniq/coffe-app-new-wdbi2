package ai.shreds.shared;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Data Transfer Object for Category.
 * Acts as a DTO to transfer category data between layers,
 * without exposing internal domain models directly.
 */
@Getter
@Builder
@AllArgsConstructor
@ToString
public class SharedCategoryDTO {
    private final UUID id;
    private final String name;
    private final String description;
    private final UUID parentCategoryId;
    private final List<String> tags;
    private final Map<String, Object> metadata;
    private final Timestamp createdAt;
    private final Timestamp updatedAt;
}
