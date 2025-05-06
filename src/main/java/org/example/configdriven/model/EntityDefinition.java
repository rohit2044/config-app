package org.example.configdriven.model;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class EntityDefinition {
    private String name;
    private String tableName;
    private List<FieldDefinition> fields;
    private Map<String, String> queries;
}