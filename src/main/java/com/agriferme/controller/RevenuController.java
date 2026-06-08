package com.agriferme.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/revenus")
public class RevenuController {

    @Autowired
    private JdbcTemplate jdbc;

    @GetMapping
    public ResponseEntity<?> getAll() {
        try {
            return ResponseEntity.ok(jdbc.queryForList("SELECT * FROM revenus ORDER BY date DESC"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            Double total = jdbc.queryForObject("SELECT COALESCE(SUM(montant), 0) FROM revenus", Double.class);
            stats.put("totalRevenus", total);
            List<Map<String, Object>> parSource = jdbc.queryForList(
                "SELECT source, SUM(montant) as total FROM revenus GROUP BY source ORDER BY total DESC"
            );
            stats.put("parSource", parSource);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, Object> body) {
        try {
            double montant = Double.parseDouble(body.get("montant").toString());
            jdbc.update(
                "INSERT INTO revenus (source, montant, date, description) VALUES (?, ?, ?, ?)",
                body.get("source"), montant, body.get("date"), body.get("description")
            );
            return ResponseEntity.ok(Map.of("message", "Revenu ajout?"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable int id, @RequestBody Map<String, Object> body) {
        try {
            double montant = Double.parseDouble(body.get("montant").toString());
            jdbc.update(
                "UPDATE revenus SET source=?, montant=?, date=?, description=? WHERE id=?",
                body.get("source"), montant, body.get("date"), body.get("description"), id
            );
            return ResponseEntity.ok(Map.of("message", "Revenu modifi?"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable int id) {
        try {
            jdbc.update("DELETE FROM revenus WHERE id=?", id);
            return ResponseEntity.ok(Map.of("message", "Revenu supprim?"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}
