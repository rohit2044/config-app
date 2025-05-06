package org.example.configdriven.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.example.configdriven.model.EntityDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class EntityConfig {

    @Bean
    public List<EntityDefinition> entityDefinitions() throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        return Arrays.asList(
                mapper.readValue(new ClassPathResource("config/entities.yml").getFile(), EntityDefinition[].class)
        );
    }

    @Bean
    public Map<String, String> queries() throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        Map<String, Object> queryMap = mapper.readValue(new ClassPathResource("config/queries.yml").getFile(), Map.class);
        Map<String, String> queries = new HashMap<>();
        
        queryMap.forEach((key, value) -> {
            if (value instanceof String) {
                queries.put(key, (String) value);
            }
        });
        
        return queries;
    }
}