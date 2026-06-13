package com.agriferme.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired private JdbcTemplate jdbc;

    @GetMapping
    public ResponseEntity<?> getNotifications(HttpServletRequest request) {
        try {
            int userId = (int) request.getAttribute("userId");
            String role = (String) request.getAttribute("role");
            List<Map<String, Object>> notifications = new ArrayList<>();

            // Check low stock
            String stockSql = "ADMIN".equals(role) ?
                "SELECT nom_produit, quantite, seuil_alerte FROM stocks WHERE quantite <= seuil_alerte" :
                "SELECT nom_produit, quantite, seuil_alerte FROM stocks WHERE quantite <= seuil_alerte AND utilisateur_id=?";

            List<Map<String, Object>> lowStocks = "ADMIN".equals(role) ?
                jdbc.queryForList(stockSql) :
                jdbc.queryForList(stockSql, userId);

            for (Map<String, Object> s : lowStocks) {
                notifications.add(Map.of(
                    "type", "stock",
                    "severity", "warning",
                    "message", "Stock bas: " + s.get("nom_produit") + " (" + s.get("quantite") + " restant)",
                    "icon", "package"
                ));
            }

            // Check sick animals
            String cheptelSql = "ADMIN".equals(role) ?
                "SELECT nom, etat_sante, maladie FROM cheptels WHERE etat_sante IN ('Malade','Critique')" :
                "SELECT nom, etat_sante, maladie FROM cheptels WHERE etat_sante IN ('Malade','Critique') AND utilisateur_id=?";

            List<Map<String, Object>> sickAnimals = "ADMIN".equals(role) ?
                jdbc.queryForList(cheptelSql) :
                jdbc.queryForList(cheptelSql, userId);

            for (Map<String, Object> a : sickAnimals) {
                notifications.add(Map.of(
                    "type", "cheptel",
                    "severity", "error",
                    "message", "Animal malade: " + a.get("nom") + " - " + a.get("etat_sante"),
                    "icon", "heart"
                ));
            }

            // Check cultures expiring soon (within 30 days)
            String cultureSql = "ADMIN".equals(role) ?
                "SELECT nom, date_recolte_prevue FROM cultures WHERE date_recolte_prevue BETWEEN CURRENT_DATE AND CURRENT_DATE + INTERVAL '30 days'" :
                "SELECT nom, date_recolte_prevue FROM cultures WHERE date_recolte_prevue BETWEEN CURRENT_DATE AND CURRENT_DATE + INTERVAL '30 days' AND utilisateur_id=?";

            List<Map<String, Object>> soonCultures = "ADMIN".equals(role) ?
                jdbc.queryForList(cultureSql) :
                jdbc.queryForList(cultureSql, userId);

            for (Map<String, Object> c : soonCultures) {
                notifications.add(Map.of(
                    "type", "culture",
                    "severity", "info",
                    "message", "Recolte proche: " + c.get("nom") + " (" + c.get("date_recolte_prevue") + ")",
                    "icon", "wheat"
                ));
            }

            return ResponseEntity.ok(Map.of(
                "notifications", notifications,
                "count", notifications.size()
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}