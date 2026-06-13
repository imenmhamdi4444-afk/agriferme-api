package com.agriferme.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api/auth")
public class ForgotPasswordController {

    @Autowired private JdbcTemplate jdbc;
    @Autowired private BCryptPasswordEncoder passwordEncoder;

    // Step 1: Request reset code
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        if (email == null || email.isBlank())
            return ResponseEntity.badRequest().body(Map.of("error", "Email requis"));

        // Check user exists
        List<Map<String, Object>> users = jdbc.queryForList(
            "SELECT id FROM utilisateurs WHERE email = ?", email
        );
        if (users.isEmpty())
            return ResponseEntity.status(404).body(Map.of("error", "Aucun compte avec cet email"));

        // Generate 6-digit code
        String code = String.format("%06d", new Random().nextInt(999999));

        // Delete old codes for this email
        jdbc.update("DELETE FROM reset_codes WHERE email = ?", email);

        // Store new code with 15 min expiry
        Timestamp expiresAt = Timestamp.valueOf(LocalDateTime.now().plusMinutes(15));
        jdbc.update(
            "INSERT INTO reset_codes (email, code, expires_at) VALUES (?, ?, ?)",
            email, code, expiresAt
        );

        // Return code in response (frontend will send it via EmailJS)
        return ResponseEntity.ok(Map.of(
            "message", "Code genere",
            "code", code,
            "email", email
        ));
    }

    // Step 2: Verify code and reset password
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String code = body.get("code");
        String newPassword = body.get("newPassword");

        if (email == null || code == null || newPassword == null)
            return ResponseEntity.badRequest().body(Map.of("error", "Tous les champs sont requis"));

        if (newPassword.length() < 6)
            return ResponseEntity.badRequest().body(Map.of("error", "Mot de passe trop court (min 6 caracteres)"));

        // Verify code
        List<Map<String, Object>> codes = jdbc.queryForList(
            "SELECT id, expires_at, used FROM reset_codes WHERE email = ? AND code = ? AND used = FALSE",
            email, code
        );

        if (codes.isEmpty())
            return ResponseEntity.status(400).body(Map.of("error", "Code invalide ou expire"));

        // Check expiry
        Timestamp expiresAt = (Timestamp) codes.get(0).get("expires_at");
        if (expiresAt.before(new Timestamp(System.currentTimeMillis())))
            return ResponseEntity.status(400).body(Map.of("error", "Code expire. Demandez un nouveau code."));

        // Mark code as used
        jdbc.update("UPDATE reset_codes SET used = TRUE WHERE email = ? AND code = ?", email, code);

        // Update password
        String hashed = passwordEncoder.encode(newPassword);
        jdbc.update("UPDATE utilisateurs SET mot_de_passe = ? WHERE email = ?", hashed, email);

        return ResponseEntity.ok(Map.of("message", "Mot de passe reinitialise avec succes"));
    }
}