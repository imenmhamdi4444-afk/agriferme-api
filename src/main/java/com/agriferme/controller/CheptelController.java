package com.agriferme.controller;

import com.agriferme.model.Cheptel;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cheptels")
public class CheptelController {

    @Autowired private JdbcTemplate jdbc;

    @GetMapping
    public ResponseEntity<?> getAll(HttpServletRequest request) {
        int userId = (int) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");
        List<Map<String, Object>> rows;
        if ("ADMIN".equals(role)) {
            rows = jdbc.queryForList("SELECT * FROM cheptels ORDER BY id");
        } else {
            rows = jdbc.queryForList("SELECT * FROM cheptels WHERE utilisateur_id=? ORDER BY id", userId);
        }
        return ResponseEntity.ok(rows);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Cheptel c, HttpServletRequest request) {
        try {
            int userId = (int) request.getAttribute("userId");
            double qte = c.getQuantiteVendue() != null ? c.getQuantiteVendue() : 0.0;
            double prix = c.getPrixUnitaire() != null ? c.getPrixUnitaire() : 0.0;
            Date dateNaissance = c.getDateNaissance() != null && !c.getDateNaissance().isEmpty() ? Date.valueOf(c.getDateNaissance()) : null;
            jdbc.update(
                "INSERT INTO cheptels (nom, type_animal, date_naissance, etat_sante, maladie, quantite_vendue, prix_unitaire, prix_total, utilisateur_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                c.getNom(), c.getTypeAnimal(), dateNaissance,
                c.getEtatSante() != null ? c.getEtatSante() : "Bon",
                c.getMaladie() != null ? c.getMaladie() : "",
                qte, prix, qte * prix, userId
            );
            return ResponseEntity.ok(Map.of("message", "Animal ajoute"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable int id, @RequestBody Cheptel c, HttpServletRequest request) {
        try {
            int userId = (int) request.getAttribute("userId");
            double qte = c.getQuantiteVendue() != null ? c.getQuantiteVendue() : 0.0;
            double prix = c.getPrixUnitaire() != null ? c.getPrixUnitaire() : 0.0;
            Date dateNaissance = c.getDateNaissance() != null && !c.getDateNaissance().isEmpty() ? Date.valueOf(c.getDateNaissance()) : null;
            jdbc.update(
                "UPDATE cheptels SET nom=?, type_animal=?, date_naissance=?, etat_sante=?, maladie=?, quantite_vendue=?, prix_unitaire=?, prix_total=? WHERE id=? AND utilisateur_id=?",
                c.getNom(), c.getTypeAnimal(), dateNaissance,
                c.getEtatSante() != null ? c.getEtatSante() : "Bon",
                c.getMaladie() != null ? c.getMaladie() : "",
                qte, prix, qte * prix, id, userId
            );
            return ResponseEntity.ok(Map.of("message", "Animal modifie"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable int id, HttpServletRequest request) {
        try {
            int userId = (int) request.getAttribute("userId");
            jdbc.update("DELETE FROM cheptels WHERE id=? AND utilisateur_id=?", id, userId);
            return ResponseEntity.ok(Map.of("message", "Animal supprime"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}