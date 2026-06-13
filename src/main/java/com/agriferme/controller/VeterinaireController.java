package com.agriferme.controller;

import com.agriferme.model.Veterinaire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/veterinaires")
public class VeterinaireController {

    @Autowired private JdbcTemplate jdbc;

    @GetMapping
    public ResponseEntity<?> getAll(@RequestParam(required = false) String ville,
                                    @RequestParam(required = false) String specialite) {
        try {
            String sql = "SELECT * FROM veterinaires WHERE statut='ACTIF'";
            if (ville != null && !ville.isBlank()) sql += " AND LOWER(ville) LIKE LOWER('%" + ville + "%')";
            if (specialite != null && !specialite.isBlank()) sql += " AND LOWER(specialite) LIKE LOWER('%" + specialite + "%')";
            sql += " ORDER BY nom";
            return ResponseEntity.ok(jdbc.queryForList(sql));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllAdmin() {
        return ResponseEntity.ok(jdbc.queryForList("SELECT * FROM veterinaires ORDER BY id"));
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Veterinaire v) {
        try {
            jdbc.update(
                "INSERT INTO veterinaires (nom, email, telephone, specialite, ville, statut) VALUES (?, ?, ?, ?, ?, 'ACTIF')",
                v.getNom(), v.getEmail(), v.getTelephone(),
                v.getSpecialite() != null ? v.getSpecialite() : "Generaliste",
                v.getVille() != null ? v.getVille() : ""
            );
            return ResponseEntity.ok(Map.of("message", "Veterinaire ajoute"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable int id, @RequestBody Veterinaire v) {
        try {
            jdbc.update(
                "UPDATE veterinaires SET nom=?, email=?, telephone=?, specialite=?, ville=? WHERE id=?",
                v.getNom(), v.getEmail(), v.getTelephone(),
                v.getSpecialite(), v.getVille(), id
            );
            return ResponseEntity.ok(Map.of("message", "Veterinaire modifie"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/activer")
    public ResponseEntity<?> activer(@PathVariable int id) {
        jdbc.update("UPDATE veterinaires SET statut='ACTIF' WHERE id=?", id);
        return ResponseEntity.ok(Map.of("message", "Veterinaire active"));
    }

    @PutMapping("/{id}/desactiver")
    public ResponseEntity<?> desactiver(@PathVariable int id) {
        jdbc.update("UPDATE veterinaires SET statut='INACTIF' WHERE id=?", id);
        return ResponseEntity.ok(Map.of("message", "Veterinaire desactive"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable int id) {
        jdbc.update("DELETE FROM veterinaires WHERE id=?", id);
        return ResponseEntity.ok(Map.of("message", "Veterinaire supprime"));
    }
}