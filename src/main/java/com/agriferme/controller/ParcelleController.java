package com.agriferme.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/parcelles")
public class ParcelleController {

    @Autowired
    private JdbcTemplate jdbc;

    @GetMapping
    public ResponseEntity<?> getAll() {
        try {
            return ResponseEntity.ok(jdbc.queryForList("SELECT * FROM parcelles ORDER BY id"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, Object> body) {
        try {
            jdbc.update(
                "INSERT INTO parcelles (nom, surface, localisation, culture_actuelle) VALUES (?, ?, ?, ?)",
                body.get("nom"), body.get("surface"), body.get("localisation"), body.get("cultureActuelle")
            );
            return ResponseEntity.ok(Map.of("message", "Parcelle ajout?e"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable int id, @RequestBody Map<String, Object> body) {
        try {
            jdbc.update(
                "UPDATE parcelles SET nom=?, surface=?, localisation=?, culture_actuelle=? WHERE id=?",
                body.get("nom"), body.get("surface"), body.get("localisation"), body.get("cultureActuelle"), id
            );
            return ResponseEntity.ok(Map.of("message", "Parcelle modifi?e"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable int id) {
        try {
            jdbc.update("DELETE FROM parcelles WHERE id=?", id);
            return ResponseEntity.ok(Map.of("message", "Parcelle supprim?e"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}
