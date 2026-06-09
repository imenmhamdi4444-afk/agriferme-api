package com.agriferme.controller;

import com.agriferme.model.Stock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stocks")
public class StockController {

    @Autowired
    private JdbcTemplate jdbc;

    @GetMapping
    public ResponseEntity<?> getAll() {
        List<Map<String, Object>> rows = jdbc.queryForList(
            "SELECT id, nom_produit, quantite, unite, seuil_alerte, prix_unitaire, depense, prix_total FROM stocks ORDER BY id"
        );
        return ResponseEntity.ok(rows);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Stock s) {
        try {
            double qte = s.getQuantite() != null ? s.getQuantite() : 0.0;
            double prix = s.getPrixUnitaire() != null ? s.getPrixUnitaire() : 0.0;
            double seuil = s.getSeuilAlerte() != null ? s.getSeuilAlerte() : 0.0;
            double depense = s.getDepense() != null ? s.getDepense() : 0.0;

            jdbc.update(
                "INSERT INTO stocks (nom_produit, quantite, unite, seuil_alerte, prix_unitaire, depense, prix_total) VALUES (?, ?, ?, ?, ?, ?, ?)",
                s.getNomProduit(), qte,
                s.getUnite() != null ? s.getUnite() : "kg",
                seuil, prix, depense, qte * prix
            );
            return ResponseEntity.ok(Map.of("message", "Stock ajoute"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable int id, @RequestBody Stock s) {
        try {
            double qte = s.getQuantite() != null ? s.getQuantite() : 0.0;
            double prix = s.getPrixUnitaire() != null ? s.getPrixUnitaire() : 0.0;
            double seuil = s.getSeuilAlerte() != null ? s.getSeuilAlerte() : 0.0;
            double depense = s.getDepense() != null ? s.getDepense() : 0.0;

            jdbc.update(
                "UPDATE stocks SET nom_produit=?, quantite=?, unite=?, seuil_alerte=?, prix_unitaire=?, depense=?, prix_total=? WHERE id=?",
                s.getNomProduit(), qte,
                s.getUnite() != null ? s.getUnite() : "kg",
                seuil, prix, depense, qte * prix, id
            );
            return ResponseEntity.ok(Map.of("message", "Stock modifie"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable int id) {
        try {
            jdbc.update("DELETE FROM stocks WHERE id=?", id);
            return ResponseEntity.ok(Map.of("message", "Stock supprime"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}