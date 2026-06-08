package com.agriferme.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/stocks")
public class StockController {

    @Autowired
    private JdbcTemplate jdbc;

    @GetMapping
    public ResponseEntity<?> getAll() {
        try {
            return ResponseEntity.ok(jdbc.queryForList("SELECT * FROM stocks ORDER BY id"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, Object> body) {
        try {
            double qte = Double.parseDouble(body.get("quantite").toString());
            double prix = Double.parseDouble(body.get("prixUnitaire").toString());
            double seuil = body.get("seuilAlerte") != null ? Double.parseDouble(body.get("seuilAlerte").toString()) : 0;
            double depense = body.get("depense") != null ? Double.parseDouble(body.get("depense").toString()) : 0;
            jdbc.update(
                "INSERT INTO stocks (nom_produit, quantite, unite, seuil_alerte, prix_unitaire, depense, prix_total) VALUES (?, ?, ?, ?, ?, ?, ?)",
                body.get("nomProduit"), qte, body.get("unite"), seuil, prix, depense, qte * prix
            );
            return ResponseEntity.ok(Map.of("message", "Stock ajout?"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable int id, @RequestBody Map<String, Object> body) {
        try {
            double qte = Double.parseDouble(body.get("quantite").toString());
            double prix = Double.parseDouble(body.get("prixUnitaire").toString());
            double seuil = body.get("seuilAlerte") != null ? Double.parseDouble(body.get("seuilAlerte").toString()) : 0;
            double depense = body.get("depense") != null ? Double.parseDouble(body.get("depense").toString()) : 0;
            jdbc.update(
                "UPDATE stocks SET nom_produit=?, quantite=?, unite=?, seuil_alerte=?, prix_unitaire=?, depense=?, prix_total=? WHERE id=?",
                body.get("nomProduit"), qte, body.get("unite"), seuil, prix, depense, qte * prix, id
            );
            return ResponseEntity.ok(Map.of("message", "Stock modifi?"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable int id) {
        try {
            jdbc.update("DELETE FROM stocks WHERE id=?", id);
            return ResponseEntity.ok(Map.of("message", "Stock supprim?"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}
