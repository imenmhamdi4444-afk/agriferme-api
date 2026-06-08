package com.agriferme.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/cultures")
public class CultureController {

    @Autowired
    private JdbcTemplate jdbc;

    @GetMapping
    public ResponseEntity<?> getAll() {
        try {
            return ResponseEntity.ok(jdbc.queryForList("SELECT * FROM cultures ORDER BY id"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, Object> body) {
        try {
            jdbc.update(
                "INSERT INTO cultures (nom, date_semis, date_recolte_prevue, parcelle_nom, statut) VALUES (?, ?, ?, ?, ?)",
                body.get("nom"), body.get("dateSemis"), body.get("dateRecoltePrevue"),
                body.get("parcelleNom"), body.get("statut")
            );
            return ResponseEntity.ok(Map.of("message", "Culture ajout?e"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable int id, @RequestBody Map<String, Object> body) {
        try {
            jdbc.update(
                "UPDATE cultures SET nom=?, date_semis=?, date_recolte_prevue=?, parcelle_nom=?, statut=? WHERE id=?",
                body.get("nom"), body.get("dateSemis"), body.get("dateRecoltePrevue"),
                body.get("parcelleNom"), body.get("statut"), id
            );
            return ResponseEntity.ok(Map.of("message", "Culture modifi?e"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable int id) {
        try {
            jdbc.update("DELETE FROM cultures WHERE id=?", id);
            return ResponseEntity.ok(Map.of("message", "Culture supprim?e"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}
