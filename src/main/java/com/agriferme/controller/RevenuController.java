package com.agriferme.controller;

import com.agriferme.model.Revenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/revenus")
public class RevenuController {

    @Autowired
    private JdbcTemplate jdbc;

    @GetMapping
    public ResponseEntity<?> getAll() {
        List<Map<String, Object>> rows = jdbc.queryForList(
            "SELECT id, source, montant, date, description FROM revenus ORDER BY date DESC"
        );
        return ResponseEntity.ok(rows);
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        Double total = jdbc.queryForObject(
            "SELECT COALESCE(SUM(montant), 0) FROM revenus", Double.class
        );
        List<Map<String, Object>> parSource = jdbc.queryForList(
            "SELECT source, SUM(montant) as total FROM revenus GROUP BY source ORDER BY total DESC"
        );
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRevenus", total);
        stats.put("parSource", parSource);
        return ResponseEntity.ok(stats);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Revenu r) {
        try {
            Date date = r.getDate() != null && !r.getDate().isEmpty()
                ? Date.valueOf(r.getDate()) : new Date(System.currentTimeMillis());

            jdbc.update(
                "INSERT INTO revenus (source, montant, date, description) VALUES (?, ?, ?, ?)",
                r.getSource(),
                r.getMontant() != null ? r.getMontant() : 0.0,
                date,
                r.getDescription() != null ? r.getDescription() : ""
            );
            return ResponseEntity.ok(Map.of("message", "Revenu ajoute"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable int id, @RequestBody Revenu r) {
        try {
            Date date = r.getDate() != null && !r.getDate().isEmpty()
                ? Date.valueOf(r.getDate()) : new Date(System.currentTimeMillis());

            jdbc.update(
                "UPDATE revenus SET source=?, montant=?, date=?, description=? WHERE id=?",
                r.getSource(),
                r.getMontant() != null ? r.getMontant() : 0.0,
                date,
                r.getDescription() != null ? r.getDescription() : "",
                id
            );
            return ResponseEntity.ok(Map.of("message", "Revenu modifie"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable int id) {
        try {
            jdbc.update("DELETE FROM revenus WHERE id=?", id);
            return ResponseEntity.ok(Map.of("message", "Revenu supprime"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}