package com.wordpressclone.domain.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.UUID;
import java.util.List;
import java.util.Map;
import java.sql.Timestamp; // Updated import from java.time.Instant to java.sql.Timestamp

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"parentCategory", "subcategories"})
@ToString(exclude = {"parentCategory", "subcategories"})
public class DomainEntityCategory {
    private UUID id;
    private String name;
    private String description;
    private DomainEntityCategory parentCategory;
    private List<String> tags;
    private Map<String, Object> metadata;
    private Timestamp createdAt; // Changed from Instant to Timestamp to match UML
    private Timestamp updatedAt; // Changed from Instant to Timestamp to match UML
    private List<DomainEntityCategory> subcategories;
}