package org.example.configdriven.controller;

import lombok.RequiredArgsConstructor;
import org.example.configdriven.service.GenericService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class GenericController {

    private final GenericService genericService;

    @GetMapping("/{entity}")
    public ResponseEntity<List<Map<String, Object>>> getAll(@PathVariable String entity) {
        return ResponseEntity.ok(genericService.executeQuery(entity, "findAll", Map.of()));
    }

    @GetMapping("/{entity}/{id}")
    public ResponseEntity<List<Map<String, Object>>> getById(@PathVariable String entity, @PathVariable Long id) {
        return ResponseEntity.ok(genericService.executeQuery(entity, "findById", Map.of("id", id)));
    }

    @PostMapping("/{entity}")
    public ResponseEntity<List<Map<String, Object>>> create(@PathVariable String entity, @RequestBody Map<String, Object> data) {
        return ResponseEntity.ok(genericService.executeQuery(entity, "create", data));
    }

    @PutMapping("/{entity}/{id}")
    public ResponseEntity<List<Map<String, Object>>> update(@PathVariable String entity, @PathVariable Long id, 
                                                            @RequestBody Map<String, Object> data) {
        data.put("id", id);
        return ResponseEntity.ok(genericService.executeQuery(entity, "update", data));
    }

    @DeleteMapping("/{entity}/{id}")
    public ResponseEntity<List<Map<String, Object>>> delete(@PathVariable String entity, @PathVariable Long id) {
        return ResponseEntity.ok(genericService.executeQuery(entity, "delete", Map.of("id", id)));
    }

    @PostMapping("/{entity}/query/{operation}")
    public ResponseEntity<List<Map<String, Object>>> customQuery(@PathVariable String entity, 
                                                                 @PathVariable String operation,
                                                                 @RequestBody Map<String, Object> params) {
        return ResponseEntity.ok(genericService.executeQuery(entity, operation, params));
    }
}