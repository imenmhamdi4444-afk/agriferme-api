package com.agriferme.controller;

import com.agriferme.model.Revenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/revenus")
public class RevenuController {

    @Autowired private JdbcTemplate jdbc;

    @GetMapping
    public ResponseEntity<?> getAll(HttpServletRequest request) {
        int userId = (int) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");
        List<Map<String, Object>> rows;
        if ("ADMIN".equals(role)) {
            rows = jdbc.queryForList("SELECT * FROM revenus ORDER BY date DESC");
        } else {
            rows = jdbc.queryForList("SELECT * FROM revenus WHERE utilisateur_id=? ORDER BY date DESC", userId);
        }
        return ResponseEntity.ok(rows);
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getStats(HttpServletRequest request) {
        int userId = (int) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");
        String where = "ADMIN".equals(role) ? "" : " WHERE utilisateur_id=" + userId;
        Double total = jdbc.queryForObject("SELECT COALESCE(SUM(montant), 0) FROM revenus" + where, Double.class);
        List<Map<String, Object>> parSource = jdbc.queryForList(
            "SELECT source, SUM(montant) as total FROM revenus" + where + " GROUP BY source ORDER BY total DESC");
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRevenus", total);
        stats.put("parSource", parSource);
        return ResponseEntity.ok(stats);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Revenu r, HttpServletRequest request) {
        try {
            int userId = (int) request.getAttribute("userId");
            Date date = r.getDate() != null && !r.getDate().isEmpty()
                ? Date.valueOf(r.getDate()) : new Date(System.currentTimeMillis());
            jdbc.update(
                "INSERT INTO revenus (source, montant, date, description, utilisateur_id) VALUES (?, ?, ?, ?, ?)",
                r.getSource(),
                r.getMontant() != null ? r.getMontant() : 0.0,
                date,
                r.getDescription() != null ? r.getDescription() : "",
                userId
            );
            return ResponseEntity.ok(Map.of("message", "Revenu ajoute"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable int id, @RequestBody Revenu r, HttpServletRequest request) {
        try {
            int userId = (int) request.getAttribute("userId");
            Date date = r.getDate() != null && !r.getDate().isEmpty()
                ? Date.valueOf(r.getDate()) : new Date(System.currentTimeMillis());
            jdbc.update(
                "UPDATE revenus SET source=?, montant=?, date=?, description=? WHERE id=? AND utilisateur_id=?",
                r.getSource(),
                r.getMontant() != null ? r.getMontant() : 0.0,
                date,
                r.getDescription() != null ? r.getDescription() : "",
                id, userId
            );
            return ResponseEntity.ok(Map.of("message", "Revenu modifie"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable int id, HttpServletRequest request) {
        try {
            int userId = (int) request.getAttribute("userId");
            jdbc.update("DELETE FROM revenus WHERE id=? AND utilisateur_id=?", id, userId);
            return ResponseEntity.ok(Map.of("message", "Revenu supprime"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}