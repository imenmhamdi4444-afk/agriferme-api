package com.agriferme.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    private static final String GROQ_URL = "https://api.groq.com/openai/v1/chat/completions";

    @Value("${gemini.api.key}")
    private String apiKey;

    private final RestTemplate rest = new RestTemplate();

    public String chat(String userMessage) {
        String systemPrompt =
            "Tu es un vétérinaire IA expert en santé animale (bovins, ovins, caprins, volailles). " +
            "Pour chaque cas : 1. Analyse les symptômes donnés par l'agriculteur " +
            "2. Diagnostique la maladie probable 3. Donne les solutions et traitements possibles " +
            "4. Indique clairement si c'est urgent et si un RDV vétérinaire est nécessaire. " +
            "Réponds en français de façon claire et concise.";

        Map<String, Object> requestBody = Map.of(
            "model", "llama-3.3-70b-versatile",
            "messages", List.of(
                Map.of("role", "system", "content", systemPrompt),
                Map.of("role", "user", "content", userMessage)
            )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = rest.exchange(GROQ_URL, HttpMethod.POST, entity, Map.class);
            Map body = response.getBody();
            if (body != null && body.containsKey("choices")) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) body.get("choices");
                if (!choices.isEmpty()) {
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    if (message != null) {
                        return (String) message.get("content");
                    }
                }
            }
            return "Désolé, je n'ai pas pu analyser les symptômes.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Erreur : " + e.getMessage();
        }
    }
}
