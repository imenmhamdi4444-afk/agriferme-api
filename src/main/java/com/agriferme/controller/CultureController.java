package com.agriferme.controller;

import com.agriferme.model.Culture;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cultures")
public class CultureController {

    @Autowired private JdbcTemplate jdbc;

    @GetMapping
    public ResponseEntity<?> getAll(HttpServletRequest request) {
        int userId = (int) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");
        List<Map<String, Object>> rows;
        if ("ADMIN".equals(role)) {
            rows = jdbc.queryForList("SELECT * FROM cultures ORDER BY id");
        } else {
            rows = jdbc.queryForList("SELECT * FROM cultures WHERE utilisateur_id=? ORDER BY id", userId);
        }
        return ResponseEntity.ok(rows);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Culture c, HttpServletRequest request) {
        try {
            int userId = (int) request.getAttribute("userId");
            Date dateSemis = c.getDateSemis() != null && !c.getDateSemis().isEmpty() ? Date.valueOf(c.getDateSemis()) : null;
            Date dateRecolte = c.getDateRecoltePrevue() != null && !c.getDateRecoltePrevue().isEmpty() ? Date.valueOf(c.getDateRecoltePrevue()) : null;
            jdbc.update(
                "INSERT INTO cultures (nom, date_semis, date_recolte_prevue, parcelle_id, parcelle_nom, statut, utilisateur_id) VALUES (?, ?, ?, ?, ?, ?, ?)",
                c.getNom(), dateSemis, dateRecolte, c.getParcelleId(),
                c.getParcelleNom() != null ? c.getParcelleNom() : "",
                c.getStatut() != null ? c.getStatut() : "Planifiee", userId
            );
            return ResponseEntity.ok(Map.of("message", "Culture ajoutee"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable int id, @RequestBody Culture c, HttpServletRequest request) {
        try {
            int userId = (int) request.getAttribute("userId");
            Date dateSemis = c.getDateSemis() != null && !c.getDateSemis().isEmpty() ? Date.valueOf(c.getDateSemis()) : null;
            Date dateRecolte = c.getDateRecoltePrevue() != null && !c.getDateRecoltePrevue().isEmpty() ? Date.valueOf(c.getDateRecoltePrevue()) : null;
            jdbc.update(
                "UPDATE cultures SET nom=?, date_semis=?, date_recolte_prevue=?, parcelle_id=?, parcelle_nom=?, statut=? WHERE id=? AND utilisateur_id=?",
                c.getNom(), dateSemis, dateRecolte, c.getParcelleId(),
                c.getParcelleNom() != null ? c.getParcelleNom() : "",
                c.getStatut() != null ? c.getStatut() : "Planifiee", id, userId
            );
            return ResponseEntity.ok(Map.of("message", "Culture modifiee"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable int id, HttpServletRequest request) {
        try {
            int userId = (int) request.getAttribute("userId");
            jdbc.update("DELETE FROM cultures WHERE id=? AND utilisateur_id=?", id, userId);
            return ResponseEntity.ok(Map.of("message", "Culture supprimee"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}