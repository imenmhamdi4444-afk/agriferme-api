package com.agriferme.controller;

import com.agriferme.model.Utilisateur;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/utilisateurs")
public class UtilisateurController {

    @Autowired
    private JdbcTemplate jdbc;

    @GetMapping
    public ResponseEntity<?> getAll() {
        List<Map<String, Object>> rows = jdbc.queryForList(
            "SELECT id, nom_complet, email, role, telephone, statut FROM utilisateurs ORDER BY id"
        );
        return ResponseEntity.ok(rows);
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", jdbc.queryForObject("SELECT COUNT(*) FROM utilisateurs", Long.class));
        stats.put("actifs", jdbc.queryForObject("SELECT COUNT(*) FROM utilisateurs WHERE statut='ACTIF'", Long.class));
        stats.put("inactifs", jdbc.queryForObject("SELECT COUNT(*) FROM utilisateurs WHERE statut='INACTIF'", Long.class));
        stats.put("admins", jdbc.queryForObject("SELECT COUNT(*) FROM utilisateurs WHERE role='ADMIN'", Long.class));
        stats.put("agriculteurs", jdbc.queryForObject("SELECT COUNT(*) FROM utilisateurs WHERE role='USER'", Long.class));
        return ResponseEntity.ok(stats);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Utilisateur u) {
        try {
            jdbc.update(
                "INSERT INTO utilisateurs (nom_complet, email, mot_de_passe, role, telephone, statut) VALUES (?, ?, ?, ?, ?, 'ACTIF')",
                u.getNomComplet(), u.getEmail(), u.getMotDePasse(),
                u.getRole() != null ? u.getRole() : "USER",
                u.getTelephone() != null ? u.getTelephone() : ""
            );
            return ResponseEntity.ok(Map.of("message", "Utilisateur ajoute"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable int id, @RequestBody Utilisateur u) {
        try {
            jdbc.update(
                "UPDATE utilisateurs SET nom_complet=?, email=?, role=?, telephone=? WHERE id=?",
                u.getNomComplet(), u.getEmail(),
                u.getRole() != null ? u.getRole() : "USER",
                u.getTelephone() != null ? u.getTelephone() : "", id
            );
            return ResponseEntity.ok(Map.of("message", "Utilisateur modifie"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable int id) {
        try {
            jdbc.update("DELETE FROM utilisateurs WHERE id=?", id);
            return ResponseEntity.ok(Map.of("message", "Utilisateur supprime"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/activer")
    public ResponseEntity<?> activer(@PathVariable int id) {
        try {
            jdbc.update("UPDATE utilisateurs SET statut='ACTIF' WHERE id=?", id);
            return ResponseEntity.ok(Map.of("message", "Acces accorde"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/bloquer")
    public ResponseEntity<?> bloquer(@PathVariable int id) {
        try {
            jdbc.update("UPDATE utilisateurs SET statut='INACTIF' WHERE id=?", id);
            return ResponseEntity.ok(Map.of("message", "Acces bloque"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}