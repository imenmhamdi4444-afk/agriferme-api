package com.agriferme.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private JdbcTemplate jdbc;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");

        if (email == null || password == null || email.isEmpty() || password.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email et mot de passe requis"));
        }

        try {
            List<Map<String, Object>> rows = jdbc.queryForList(
                "SELECT id, nom_complet, role, statut FROM utilisateurs WHERE email = ? AND mot_de_passe = ?",
                email, password
            );

            if (rows.isEmpty()) {
                return ResponseEntity.status(401).body(Map.of("error", "Email ou mot de passe incorrect"));
            }

            Map<String, Object> user = rows.get(0);
            String statut = (String) user.get("statut");

            if ("INACTIF".equals(statut)) {
                return ResponseEntity.status(403).body(Map.of("error", "Compte bloque. Contactez l'administrateur."));
            }

            Map<String, Object> response = new HashMap<>();
            response.put("id", user.get("id"));
            response.put("nomComplet", user.get("nom_complet"));
            response.put("email", email);
            response.put("role", user.get("role"));
            response.put("statut", statut);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Erreur serveur: " + e.getMessage()));
        }
    }
}
