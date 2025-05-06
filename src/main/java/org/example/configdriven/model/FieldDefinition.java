package org.example.configdriven.model;

import lombok.Data;

@Data
public class FieldDefinition {
    private String name;
    private String type;
    private boolean isPrimaryKey;
    private boolean isNullable;
    private String defaultValue;
}