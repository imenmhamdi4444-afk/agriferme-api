package com.agriferme.controller;

import com.agriferme.model.Veterinaire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/veterinaires")
public class VeterinaireController {

    @Autowired private JdbcTemplate jdbc;

    @GetMapping
    public ResponseEntity<?> getAll(
            @RequestParam(required = false) String ville,
            @RequestParam(required = false) String specialite) {
        try {
            StringBuilder sql = new StringBuilder(
                "SELECT id, nom, email, telephone, specialite, ville, statut, commission_montant " +
                "FROM veterinaires WHERE statut='ACTIF'");
            List<Object> params = new ArrayList<>();
            if (ville != null && !ville.isBlank()) {
                sql.append(" AND LOWER(ville) LIKE LOWER(?)");
                params.add("%" + ville.trim() + "%");
            }
            if (specialite != null && !specialite.isBlank()) {
                sql.append(" AND LOWER(specialite) LIKE LOWER(?)");
                params.add("%" + specialite.trim() + "%");
            }
            sql.append(" ORDER BY nom");
            return ResponseEntity.ok(jdbc.queryForList(sql.toString(), params.toArray()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllAdmin() {
        return ResponseEntity.ok(jdbc.queryForList(
            "SELECT id, nom, email, telephone, specialite, ville, statut, commission_montant " +
            "FROM veterinaires ORDER BY id"));
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Veterinaire v) {
        try {
            double montant = v.getCommissionMontant() != null ? v.getCommissionMontant() : 0.0;
            jdbc.update(
                "INSERT INTO veterinaires (nom, email, telephone, specialite, ville, statut, commission_montant) " +
                "VALUES (?, ?, ?, ?, ?, 'ACTIF', ?)",
                v.getNom(), v.getEmail(), v.getTelephone(),
                v.getSpecialite() != null ? v.getSpecialite() : "Generaliste",
                v.getVille() != null ? v.getVille() : "",
                montant
            );
            return ResponseEntity.ok(Map.of("message", "Veterinaire ajoute"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable int id, @RequestBody Veterinaire v) {
        try {
            double montant = v.getCommissionMontant() != null ? v.getCommissionMontant() : 0.0;
            jdbc.update(
                "UPDATE veterinaires SET nom=?, email=?, telephone=?, specialite=?, ville=?, commission_montant=? " +
                "WHERE id=?",
                v.getNom(), v.getEmail(), v.getTelephone(),
                v.getSpecialite(), v.getVille(), montant, id
            );
            return ResponseEntity.ok(Map.of("message", "Veterinaire modifie"));
        } catch (Exception e) {
            e.printStackTrace();
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