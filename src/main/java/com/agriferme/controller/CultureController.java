package com.agriferme.controller;

import com.agriferme.model.Culture;
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

    @Autowired
    private JdbcTemplate jdbc;

    @GetMapping
    public ResponseEntity<?> getAll() {
        List<Map<String, Object>> rows = jdbc.queryForList(
            "SELECT id, nom, date_semis, date_recolte_prevue, parcelle_id, parcelle_nom, statut FROM cultures ORDER BY id"
        );
        return ResponseEntity.ok(rows);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Culture c) {
        try {
            Date dateSemis = c.getDateSemis() != null && !c.getDateSemis().isEmpty()
                ? Date.valueOf(c.getDateSemis()) : null;
            Date dateRecolte = c.getDateRecoltePrevue() != null && !c.getDateRecoltePrevue().isEmpty()
                ? Date.valueOf(c.getDateRecoltePrevue()) : null;

            jdbc.update(
                "INSERT INTO cultures (nom, date_semis, date_recolte_prevue, parcelle_id, parcelle_nom, statut) VALUES (?, ?, ?, ?, ?, ?)",
                c.getNom(), dateSemis, dateRecolte, c.getParcelleId(),
                c.getParcelleNom() != null ? c.getParcelleNom() : "",
                c.getStatut() != null ? c.getStatut() : "Planifiee"
            );
            return ResponseEntity.ok(Map.of("message", "Culture ajoutee"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable int id, @RequestBody Culture c) {
        try {
            Date dateSemis = c.getDateSemis() != null && !c.getDateSemis().isEmpty()
                ? Date.valueOf(c.getDateSemis()) : null;
            Date dateRecolte = c.getDateRecoltePrevue() != null && !c.getDateRecoltePrevue().isEmpty()
                ? Date.valueOf(c.getDateRecoltePrevue()) : null;

            jdbc.update(
                "UPDATE cultures SET nom=?, date_semis=?, date_recolte_prevue=?, parcelle_id=?, parcelle_nom=?, statut=? WHERE id=?",
                c.getNom(), dateSemis, dateRecolte, c.getParcelleId(),
                c.getParcelleNom() != null ? c.getParcelleNom() : "",
                c.getStatut() != null ? c.getStatut() : "Planifiee", id
            );
            return ResponseEntity.ok(Map.of("message", "Culture modifiee"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable int id) {
        try {
            jdbc.update("DELETE FROM cultures WHERE id=?", id);
            return ResponseEntity.ok(Map.of("message", "Culture supprimee"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}