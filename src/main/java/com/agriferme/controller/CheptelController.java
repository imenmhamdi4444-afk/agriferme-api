package com.agriferme.controller;

import com.agriferme.model.Cheptel;
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

    @Autowired
    private JdbcTemplate jdbc;

    @GetMapping
    public ResponseEntity<?> getAll() {
        List<Map<String, Object>> rows = jdbc.queryForList(
            "SELECT id, nom, type_animal, date_naissance, etat_sante, maladie, quantite_vendue, prix_unitaire, prix_total FROM cheptels ORDER BY id"
        );
        return ResponseEntity.ok(rows);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Cheptel c) {
        try {
            double qte = c.getQuantiteVendue() != null ? c.getQuantiteVendue() : 0.0;
            double prix = c.getPrixUnitaire() != null ? c.getPrixUnitaire() : 0.0;
            Date dateNaissance = c.getDateNaissance() != null && !c.getDateNaissance().isEmpty()
                ? Date.valueOf(c.getDateNaissance()) : null;

            jdbc.update(
                "INSERT INTO cheptels (nom, type_animal, date_naissance, etat_sante, maladie, quantite_vendue, prix_unitaire, prix_total) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                c.getNom(), c.getTypeAnimal(), dateNaissance,
                c.getEtatSante() != null ? c.getEtatSante() : "Bon",
                c.getMaladie() != null ? c.getMaladie() : "",
                qte, prix, qte * prix
            );
            return ResponseEntity.ok(Map.of("message", "Animal ajoute"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable int id, @RequestBody Cheptel c) {
        try {
            double qte = c.getQuantiteVendue() != null ? c.getQuantiteVendue() : 0.0;
            double prix = c.getPrixUnitaire() != null ? c.getPrixUnitaire() : 0.0;
            Date dateNaissance = c.getDateNaissance() != null && !c.getDateNaissance().isEmpty()
                ? Date.valueOf(c.getDateNaissance()) : null;

            jdbc.update(
                "UPDATE cheptels SET nom=?, type_animal=?, date_naissance=?, etat_sante=?, maladie=?, quantite_vendue=?, prix_unitaire=?, prix_total=? WHERE id=?",
                c.getNom(), c.getTypeAnimal(), dateNaissance,
                c.getEtatSante() != null ? c.getEtatSante() : "Bon",
                c.getMaladie() != null ? c.getMaladie() : "",
                qte, prix, qte * prix, id
            );
            return ResponseEntity.ok(Map.of("message", "Animal modifie"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable int id) {
        try {
            jdbc.update("DELETE FROM cheptels WHERE id=?", id);
            return ResponseEntity.ok(Map.of("message", "Animal supprime"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}