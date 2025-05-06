package org.example.configdriven.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.configdriven.model.EntityDefinition;
import org.example.configdriven.model.FieldDefinition;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DatabaseService {

    private final JdbcTemplate jdbcTemplate;
    private final List<EntityDefinition> entityDefinitions;

    @PostConstruct
    public void initDatabase() {
        entityDefinitions.forEach(this::createTable);
    }

    private void createTable(EntityDefinition entity) {
        String createTableQuery = generateCreateTableQuery(entity);
        log.info("Creating table with query: {}", createTableQuery);
        jdbcTemplate.execute(createTableQuery);
    }

    private String generateCreateTableQuery(EntityDefinition entity) {
        String fieldsDefinition = entity.getFields().stream()
                .map(this::fieldToSqlDefinition)
                .collect(Collectors.joining(", "));

        return String.format("CREATE TABLE IF NOT EXISTS %s (%s)", 
                entity.getTableName(), fieldsDefinition);
    }

    private String fieldToSqlDefinition(FieldDefinition field) {
        StringBuilder sb = new StringBuilder();
        sb.append(field.getName()).append(" ").append(convertToSqlType(field.getType()));
        
        if (field.isPrimaryKey()) {
            sb.append(" PRIMARY KEY");
        }
        
        if (!field.isNullable()) {
            sb.append(" NOT NULL");
        }
        
        if (field.getDefaultValue() != null && !field.getDefaultValue().isEmpty()) {
            sb.append(" DEFAULT ").append(field.getDefaultValue());
        }
        
        return sb.toString();
    }

    private String convertToSqlType(String type) {
        switch (type.toLowerCase()) {
            case "string":
            case "text":
                return "TEXT";
            case "integer":
            case "int":
                return "INTEGER";
            case "double":
            case "float":
                return "REAL";
            case "boolean":
            case "bool":
                return "BOOLEAN";
            case "date":
            case "datetime":
                return "DATETIME";
            default:
                return "TEXT";
        }
    }
}