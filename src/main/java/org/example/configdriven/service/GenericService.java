package org.example.configdriven.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.configdriven.model.EntityDefinition;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GenericService {

    private final JdbcTemplate jdbcTemplate;
    private final List<EntityDefinition> entityDefinitions;
    private final Map<String, String> queryTemplates;

    public List<Map<String, Object>> executeQuery(String entityName, String operation, Map<String, Object> params) {
        EntityDefinition entity = findEntityByName(entityName);
        if (entity == null) {
            throw new IllegalArgumentException("Entity not found: " + entityName);
        }

        String queryKey = entityName + "." + operation;
        String query = entity.getQueries() != null && entity.getQueries().containsKey(operation) 
                ? entity.getQueries().get(operation) 
                : queryTemplates.getOrDefault(operation, null);

        if (query == null) {
            // Generate default query if not found in config
            query = generateDefaultQuery(entity, operation);
        }

        // Replace table name placeholder
        query = query.replace("${table}", entity.getTableName());

        log.info("Executing query: {} with params: {}", query, params);
        
        NamedParameterJdbcTemplate namedJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        MapSqlParameterSource parameters = new MapSqlParameterSource(params);

        if (operation.equals("create")) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            namedJdbcTemplate.update(query, parameters, keyHolder);
            Map<String, Object> result = Map.of("id", keyHolder.getKey());
            return List.of(result);
        } else {
            return namedJdbcTemplate.queryForList(query, parameters);
        }
    }

    private EntityDefinition findEntityByName(String entityName) {
        Optional<EntityDefinition> entity = entityDefinitions.stream()
                .filter(e -> e.getName().equals(entityName))
                .findFirst();
        return entity.orElse(null);
    }

    private String generateDefaultQuery(EntityDefinition entity, String operation) {
        switch (operation) {
            case "findAll":
                return "SELECT * FROM ${table}";
            case "findById":
                return "SELECT * FROM ${table} WHERE id = :id";
            case "create":
                String fields = entity.getFields().stream()
                        .filter(field -> !field.isPrimaryKey() || field.getName().equals("id"))
                        .map(field -> field.getName())
                        .collect(Collectors.joining(", "));
                
                String values = entity.getFields().stream()
                        .filter(field -> !field.isPrimaryKey() || field.getName().equals("id"))
                        .map(field -> ":" + field.getName())
                        .collect(Collectors.joining(", "));
                
                return String.format("INSERT INTO ${table} (%s) VALUES (%s)", fields, values);
            case "update":
                String setClause = entity.getFields().stream()
                        .filter(field -> !field.isPrimaryKey())
                        .map(field -> field.getName() + " = :" + field.getName())
                        .collect(Collectors.joining(", "));
                
                return String.format("UPDATE ${table} SET %s WHERE id = :id", setClause);
            case "delete":
                return "DELETE FROM ${table} WHERE id = :id";
            default:
                throw new IllegalArgumentException("Unsupported operation: " + operation);
        }
    }
}