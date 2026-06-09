package com.agriferme.controller;

import com.agriferme.model.Parcelle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/parcelles")
public class ParcelleController {

    @Autowired
    private JdbcTemplate jdbc;

    @GetMapping
    public ResponseEntity<?> getAll() {
        List<Map<String, Object>> rows = jdbc.queryForList(
            "SELECT id, nom, surface, localisation, culture_actuelle FROM parcelles ORDER BY id"
        );
        return ResponseEntity.ok(rows);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Parcelle p) {
        try {
            jdbc.update(
                "INSERT INTO parcelles (nom, surface, localisation, culture_actuelle) VALUES (?, ?, ?, ?)",
                p.getNom(), p.getSurface(), p.getLocalisation(),
                p.getCultureActuelle() != null ? p.getCultureActuelle() : ""
            );
            return ResponseEntity.ok(Map.of("message", "Parcelle ajoutee"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable int id, @RequestBody Parcelle p) {
        try {
            jdbc.update(
                "UPDATE parcelles SET nom=?, surface=?, localisation=?, culture_actuelle=? WHERE id=?",
                p.getNom(), p.getSurface(), p.getLocalisation(),
                p.getCultureActuelle() != null ? p.getCultureActuelle() : "", id
            );
            return ResponseEntity.ok(Map.of("message", "Parcelle modifiee"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable int id) {
        try {
            jdbc.update("DELETE FROM parcelles WHERE id=?", id);
            return ResponseEntity.ok(Map.of("message", "Parcelle supprimee"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}