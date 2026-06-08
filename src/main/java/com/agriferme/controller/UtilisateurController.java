package com.agriferme.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/utilisateurs")
public class UtilisateurController {

    @Autowired
    private JdbcTemplate jdbc;

    @GetMapping
    public ResponseEntity<?> getAll() {
        try {
            List<Map<String, Object>> rows = jdbc.queryForList(
                "SELECT id, nom_complet, email, role, telephone, statut FROM utilisateurs"
            );
            return ResponseEntity.ok(rows);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("total", jdbc.queryForObject("SELECT COUNT(*) FROM utilisateurs", Long.class));
            stats.put("actifs", jdbc.queryForObject("SELECT COUNT(*) FROM utilisateurs WHERE statut='ACTIF'", Long.class));
            stats.put("inactifs", jdbc.queryForObject("SELECT COUNT(*) FROM utilisateurs WHERE statut='INACTIF'", Long.class));
            stats.put("admins", jdbc.queryForObject("SELECT COUNT(*) FROM utilisateurs WHERE role='ADMIN'", Long.class));
            stats.put("agriculteurs", jdbc.queryForObject("SELECT COUNT(*) FROM utilisateurs WHERE role='USER'", Long.class));
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, String> body) {
        try {
            jdbc.update(
                "INSERT INTO utilisateurs (nom_complet, email, mot_de_passe, role, telephone, statut) VALUES (?, ?, ?, ?, ?, 'ACTIF')",
                body.get("nomComplet"), body.get("email"), body.get("motDePasse"),
                body.get("role"), body.get("telephone")
            );
            return ResponseEntity.ok(Map.of("message", "Utilisateur ajout?"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable int id, @RequestBody Map<String, String> body) {
        try {
            jdbc.update(
                "UPDATE utilisateurs SET nom_complet=?, email=?, role=?, telephone=? WHERE id=?",
                body.get("nomComplet"), body.get("email"), body.get("role"), body.get("telephone"), id
            );
            return ResponseEntity.ok(Map.of("message", "Utilisateur modifi?"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable int id) {
        try {
            jdbc.update("DELETE FROM utilisateurs WHERE id=?", id);
            return ResponseEntity.ok(Map.of("message", "Utilisateur supprim?"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/activer")
    public ResponseEntity<?> activer(@PathVariable int id) {
        try {
            jdbc.update("UPDATE utilisateurs SET statut='ACTIF' WHERE id=?", id);
            return ResponseEntity.ok(Map.of("message", "Acc?s accord?"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/bloquer")
    public ResponseEntity<?> bloquer(@PathVariable int id) {
        try {
            jdbc.update("UPDATE utilisateurs SET statut='INACTIF' WHERE id=?", id);
            return ResponseEntity.ok(Map.of("message", "Acc?s bloqu?"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}
