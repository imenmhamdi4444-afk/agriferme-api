package com.agriferme.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.sql.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rendez-vous")
public class RendezVousController {

    @Autowired private JdbcTemplate jdbc;

    @GetMapping
    public ResponseEntity<?> getAll(HttpServletRequest request) {
        int userId = (int) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");
        List<Map<String, Object>> rows;
        if ("ADMIN".equals(role)) {
            rows = jdbc.queryForList("SELECT * FROM rendez_vous ORDER BY created_at DESC");
        } else {
            rows = jdbc.queryForList("SELECT * FROM rendez_vous WHERE utilisateur_id=? ORDER BY created_at DESC", userId);
        }
        return ResponseEntity.ok(rows);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        try {
            int userId = (int) request.getAttribute("userId");
            Date dateRdv = Date.valueOf(body.get("dateRdv").toString());
            Integer vetId = body.get("veterinaireId") != null ? Integer.parseInt(body.get("veterinaireId").toString()) : null;

            jdbc.update(
                "INSERT INTO rendez_vous (utilisateur_id, animal_nom, animal_type, veterinaire_id, veterinaire_nom, date_rdv, motif, statut) VALUES (?, ?, ?, ?, ?, ?, ?, 'En attente')",
                userId,
                body.get("animalNom"),
                body.get("animalType"),
                vetId,
                body.get("veterinaireNom"),
                dateRdv,
                body.get("motif")
            );
            return ResponseEntity.ok(Map.of("message", "RDV enregistre"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/statut")
    public ResponseEntity<?> updateStatut(@PathVariable int id, @RequestBody Map<String, String> body) {
        jdbc.update("UPDATE rendez_vous SET statut=? WHERE id=?", body.get("statut"), id);
        return ResponseEntity.ok(Map.of("message", "Statut mis a jour"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable int id) {
        jdbc.update("DELETE FROM rendez_vous WHERE id=?", id);
        return ResponseEntity.ok(Map.of("message", "RDV supprime"));
    }
}