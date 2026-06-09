package com.agriferme.controller;

import com.agriferme.model.Parcelle;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/parcelles")
public class ParcelleController {

    @Autowired private JdbcTemplate jdbc;

    @GetMapping
    public ResponseEntity<?> getAll(HttpServletRequest request) {
        int userId = (int) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");
        List<Map<String, Object>> rows;
        if ("ADMIN".equals(role)) {
            rows = jdbc.queryForList("SELECT * FROM parcelles ORDER BY id");
        } else {
            rows = jdbc.queryForList("SELECT * FROM parcelles WHERE utilisateur_id=? ORDER BY id", userId);
        }
        return ResponseEntity.ok(rows);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Parcelle p, HttpServletRequest request) {
        try {
            int userId = (int) request.getAttribute("userId");
            jdbc.update(
                "INSERT INTO parcelles (nom, surface, localisation, culture_actuelle, utilisateur_id) VALUES (?, ?, ?, ?, ?)",
                p.getNom(), p.getSurface(), p.getLocalisation(),
                p.getCultureActuelle() != null ? p.getCultureActuelle() : "", userId
            );
            return ResponseEntity.ok(Map.of("message", "Parcelle ajoutee"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable int id, @RequestBody Parcelle p, HttpServletRequest request) {
        try {
            int userId = (int) request.getAttribute("userId");
            jdbc.update(
                "UPDATE parcelles SET nom=?, surface=?, localisation=?, culture_actuelle=? WHERE id=? AND utilisateur_id=?",
                p.getNom(), p.getSurface(), p.getLocalisation(),
                p.getCultureActuelle() != null ? p.getCultureActuelle() : "", id, userId
            );
            return ResponseEntity.ok(Map.of("message", "Parcelle modifiee"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable int id, HttpServletRequest request) {
        try {
            int userId = (int) request.getAttribute("userId");
            jdbc.update("DELETE FROM parcelles WHERE id=? AND utilisateur_id=?", id, userId);
            return ResponseEntity.ok(Map.of("message", "Parcelle supprimee"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}