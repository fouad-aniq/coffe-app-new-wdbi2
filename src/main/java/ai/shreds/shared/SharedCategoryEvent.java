package ai.shreds.shared;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.sql.Timestamp;
import java.io.Serializable;

import ai.shreds.shared.SharedCategoryDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SharedCategoryEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private EventType eventType;
    private Timestamp timestamp;
    private SharedCategoryDTO category;
}