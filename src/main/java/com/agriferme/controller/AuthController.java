package com.agriferme.controller;

import com.agriferme.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired private JdbcTemplate jdbc;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");

        if (email == null || password == null || email.isEmpty() || password.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email et mot de passe requis"));
        }

        try {
            List<Map<String, Object>> rows = jdbc.queryForList(
                "SELECT id, nom_complet, role, statut, mot_de_passe FROM utilisateurs WHERE email = ?",
                email
            );

            if (rows.isEmpty()) {
                return ResponseEntity.status(401).body(Map.of("error", "Email ou mot de passe incorrect"));
            }

            Map<String, Object> user = rows.get(0);
            String storedPassword = (String) user.get("mot_de_passe");
            String statut = (String) user.get("statut");

            // Support both plain text (legacy) and bcrypt passwords
            boolean passwordMatch;
            if (storedPassword != null && storedPassword.startsWith("$2a$")) {
                passwordMatch = passwordEncoder.matches(password, storedPassword);
            } else {
                passwordMatch = password.equals(storedPassword);
            }

            if (!passwordMatch) {
                return ResponseEntity.status(401).body(Map.of("error", "Email ou mot de passe incorrect"));
            }

            if ("INACTIF".equals(statut)) {
                return ResponseEntity.status(403).body(Map.of("error", "Compte bloque. Contactez l'administrateur."));
            }

            int userId = ((Number) user.get("id")).intValue();
            String role = (String) user.get("role");
            String token = jwtUtil.generateToken(userId, email, role);

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("id", userId);
            response.put("nomComplet", user.get("nom_complet"));
            response.put("email", email);
            response.put("role", role);
            response.put("statut", statut);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Erreur serveur: " + e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        try {
            String hashedPassword = passwordEncoder.encode(body.get("motDePasse"));
            jdbc.update(
                "INSERT INTO utilisateurs (nom_complet, email, mot_de_passe, role, telephone, statut) VALUES (?, ?, ?, ?, ?, 'ACTIF')",
                body.get("nomComplet"), body.get("email"), hashedPassword,
                body.get("role") != null ? body.get("role") : "USER",
                body.get("telephone") != null ? body.get("telephone") : ""
            );
            return ResponseEntity.ok(Map.of("message", "Utilisateur cree"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}