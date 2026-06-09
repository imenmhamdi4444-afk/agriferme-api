package com.agriferme.controller;

import com.agriferme.model.Utilisateur;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/utilisateurs")
public class UtilisateurController {

    @Autowired private JdbcTemplate jdbc;
    @Autowired private BCryptPasswordEncoder passwordEncoder;

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
        long total = jdbc.queryForObject("SELECT COUNT(*) FROM utilisateurs", Long.class);
        long actifs = jdbc.queryForObject("SELECT COUNT(*) FROM utilisateurs WHERE statut='ACTIF'", Long.class);
        long inactifs = jdbc.queryForObject("SELECT COUNT(*) FROM utilisateurs WHERE statut='INACTIF'", Long.class);
        long admins = jdbc.queryForObject("SELECT COUNT(*) FROM utilisateurs WHERE role='ADMIN'", Long.class);
        long agriculteurs = jdbc.queryForObject("SELECT COUNT(*) FROM utilisateurs WHERE role='USER'", Long.class);
        long avecTelephone = jdbc.queryForObject("SELECT COUNT(*) FROM utilisateurs WHERE telephone IS NOT NULL AND telephone != ''", Long.class);
        stats.put("total", total);
        stats.put("actifs", actifs);
        stats.put("inactifs", inactifs);
        stats.put("admins", admins);
        stats.put("agriculteurs", agriculteurs);
        stats.put("avecTelephone", avecTelephone);
        stats.put("sansTelephone", total - avecTelephone);
        stats.put("tauxActifs", total > 0 ? Math.round((actifs * 100.0 / total) * 10.0) / 10.0 : 0);
        stats.put("tauxAdmins", total > 0 ? Math.round((admins * 100.0 / total) * 10.0) / 10.0 : 0);
        return ResponseEntity.ok(stats);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Utilisateur u) {
        try {
            String hashedPassword = passwordEncoder.encode(u.getMotDePasse());
            jdbc.update(
                "INSERT INTO utilisateurs (nom_complet, email, mot_de_passe, role, telephone, statut) VALUES (?, ?, ?, ?, ?, 'ACTIF')",
                u.getNomComplet(), u.getEmail(), hashedPassword,
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