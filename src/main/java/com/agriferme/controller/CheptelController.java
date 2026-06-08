package com.agriferme.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/cheptels")
public class CheptelController {

    @Autowired
    private JdbcTemplate jdbc;

    @GetMapping
    public ResponseEntity<?> getAll() {
        try {
            return ResponseEntity.ok(jdbc.queryForList("SELECT * FROM cheptels ORDER BY id"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, Object> body) {
        try {
            double qte = body.get("quantiteVendue") != null ? Double.parseDouble(body.get("quantiteVendue").toString()) : 0;
            double prix = body.get("prixUnitaire") != null ? Double.parseDouble(body.get("prixUnitaire").toString()) : 0;
            jdbc.update(
                "INSERT INTO cheptels (nom, type_animal, date_naissance, etat_sante, maladie, quantite_vendue, prix_unitaire, prix_total) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                body.get("nom"), body.get("typeAnimal"), body.get("dateNaissance"),
                body.get("etatSante"), body.get("maladie"), qte, prix, qte * prix
            );
            return ResponseEntity.ok(Map.of("message", "Animal ajout?"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable int id, @RequestBody Map<String, Object> body) {
        try {
            double qte = body.get("quantiteVendue") != null ? Double.parseDouble(body.get("quantiteVendue").toString()) : 0;
            double prix = body.get("prixUnitaire") != null ? Double.parseDouble(body.get("prixUnitaire").toString()) : 0;
            jdbc.update(
                "UPDATE cheptels SET nom=?, type_animal=?, date_naissance=?, etat_sante=?, maladie=?, quantite_vendue=?, prix_unitaire=?, prix_total=? WHERE id=?",
                body.get("nom"), body.get("typeAnimal"), body.get("dateNaissance"),
                body.get("etatSante"), body.get("maladie"), qte, prix, qte * prix, id
            );
            return ResponseEntity.ok(Map.of("message", "Animal modifi?"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable int id) {
        try {
            jdbc.update("DELETE FROM cheptels WHERE id=?", id);
            return ResponseEntity.ok(Map.of("message", "Animal supprim?"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}
