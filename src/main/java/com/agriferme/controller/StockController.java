package com.agriferme.controller;

import com.agriferme.model.Stock;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stocks")
public class StockController {

    @Autowired private JdbcTemplate jdbc;

    @GetMapping
    public ResponseEntity<?> getAll(HttpServletRequest request) {
        int userId = (int) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");
        List<Map<String, Object>> rows;
        if ("ADMIN".equals(role)) {
            rows = jdbc.queryForList("SELECT * FROM stocks ORDER BY id");
        } else {
            rows = jdbc.queryForList("SELECT * FROM stocks WHERE utilisateur_id=? ORDER BY id", userId);
        }
        return ResponseEntity.ok(rows);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Stock s, HttpServletRequest request) {
        try {
            int userId = (int) request.getAttribute("userId");
            double qte = s.getQuantite() != null ? s.getQuantite() : 0.0;
            double prix = s.getPrixUnitaire() != null ? s.getPrixUnitaire() : 0.0;
            double seuil = s.getSeuilAlerte() != null ? s.getSeuilAlerte() : 0.0;
            double depense = s.getDepense() != null ? s.getDepense() : 0.0;
            jdbc.update(
                "INSERT INTO stocks (nom_produit, quantite, unite, seuil_alerte, prix_unitaire, depense, prix_total, utilisateur_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                s.getNomProduit(), qte, s.getUnite() != null ? s.getUnite() : "kg",
                seuil, prix, depense, qte * prix, userId
            );
            return ResponseEntity.ok(Map.of("message", "Stock ajoute"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable int id, @RequestBody Stock s, HttpServletRequest request) {
        try {
            int userId = (int) request.getAttribute("userId");
            double qte = s.getQuantite() != null ? s.getQuantite() : 0.0;
            double prix = s.getPrixUnitaire() != null ? s.getPrixUnitaire() : 0.0;
            double seuil = s.getSeuilAlerte() != null ? s.getSeuilAlerte() : 0.0;
            double depense = s.getDepense() != null ? s.getDepense() : 0.0;
            jdbc.update(
                "UPDATE stocks SET nom_produit=?, quantite=?, unite=?, seuil_alerte=?, prix_unitaire=?, depense=?, prix_total=? WHERE id=? AND utilisateur_id=?",
                s.getNomProduit(), qte, s.getUnite() != null ? s.getUnite() : "kg",
                seuil, prix, depense, qte * prix, id, userId
            );
            return ResponseEntity.ok(Map.of("message", "Stock modifie"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable int id, HttpServletRequest request) {
        try {
            int userId = (int) request.getAttribute("userId");
            jdbc.update("DELETE FROM stocks WHERE id=? AND utilisateur_id=?", id, userId);
            return ResponseEntity.ok(Map.of("message", "Stock supprime"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}