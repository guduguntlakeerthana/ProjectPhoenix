package com.devflowai.service;

import com.devflowai.dto.response.AiChatResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Service
public class AiService {

    private static final Logger LOGGER = Logger.getLogger(AiService.class.getName());

    @Value("${gemini.api.key:}")
    private String apiKey;

    public AiChatResponse generateResponse(String prompt) {
        if (apiKey == null || apiKey.trim().isEmpty() || apiKey.startsWith("${")) {
            LOGGER.info("Gemini API key not configured. Generating simulated AI response.");
            return AiChatResponse.builder()
                    .response(getSimulatedResponse(prompt))
                    .simulated(true)
                    .build();
        }

        try {
            String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + apiKey;
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Construct payload: {"contents": [{"parts": [{"text": "prompt"}]}]}
            Map<String, Object> body = new HashMap<>();
            Map<String, Object> part = new HashMap<>();
            part.put("text", prompt);
            Map<String, Object> content = new HashMap<>();
            content.put("parts", List.of(part));
            body.put("contents", List.of(content));

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            Map<String, Object> response = restTemplate.postForObject(url, entity, Map.class);

            if (response != null && response.containsKey("candidates")) {
                List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
                if (!candidates.isEmpty()) {
                    Map<String, Object> candidate = candidates.get(0);
                    Map<String, Object> candidateContent = (Map<String, Object>) candidate.get("content");
                    List<Map<String, Object>> parts = (List<Map<String, Object>>) candidateContent.get("parts");
                    if (!parts.isEmpty()) {
                        String text = (String) parts.get(0).get("text");
                        return AiChatResponse.builder()
                                .response(text)
                                .simulated(false)
                                .build();
                    }
                }
            }

            throw new RuntimeException("Empty or malformed payload from Gemini API response");

        } catch (Exception e) {
            LOGGER.severe("Failed to communicate with Gemini API. Error: " + e.getMessage() + ". Falling back to simulation.");
            return AiChatResponse.builder()
                    .response("Error calling Gemini API: " + e.getMessage() + "\n\n[SIMULATED FALLBACK]\n" + getSimulatedResponse(prompt))
                    .simulated(true)
                    .build();
        }
    }

    private String getSimulatedResponse(String prompt) {
        String pLower = prompt.toLowerCase();
        if (pLower.contains("sprint") || pLower.contains("breakdown")) {
            return "Here is a recommended sprint breakdown for your project:\n\n" +
                    "### Sprint 1: Foundation & Security (Days 1-4)\n" +
                    "- Initialize backend project architecture and configure Docker setups.\n" +
                    "- Set up Spring Security filters, JWT authentication, and user signup APIs.\n\n" +
                    "### Sprint 2: Core Business Models & REST APIs (Days 5-8)\n" +
                    "- Establish Project, Task, and Attachment entities and database schemas.\n" +
                    "- Implement services and controllers with full boundary validation validations.\n\n" +
                    "### Sprint 3: Client Dashboard & UI Views (Days 9-12)\n" +
                    "- Design standalone Angular dashboard layouts and navbar widgets.\n" +
                    "- Integrate calendar grids, Kanban columns, and timeline components.\n\n" +
                    "### Sprint 4: CI/CD Pipeline & Audit Tracking (Days 13-14)\n" +
                    "- Log CRUD audit trails and setup email triggers on updates.\n" +
                    "- Package Docker containers and create setup documentation templates.";
        }

        if (pLower.contains("doc") || pLower.contains("generate") || pLower.contains("spec")) {
            return "Here is a draft technical specification template for your project:\n\n" +
                    "# DevFlow AI Technical Specification\n\n" +
                    "## 1. Objectives\n" +
                    "Provide a collaborative task management board empowered by AI predictions and automated timelines.\n\n" +
                    "## 2. Technical Stack\n" +
                    "- Backend: Java 17, Spring Boot 3.x, JPA, PostgreSQL, JUnit\n" +
                    "- Client: Angular 21, TypeScript, HTML/CSS layout\n\n" +
                    "## 3. Database Schema\n" +
                    "- `users`: id, email, password, role, dates\n" +
                    "- `projects`: id, title, description, user_id, dates\n" +
                    "- `tasks`: id, title, status, priority, due_date, project_id, dates\n" +
                    "- `audit_logs`: id, action, details, user_id, dates";
        }

        return "Hello! I am your DevFlow AI Assistant.\n\n" +
                "I can analyze your workflow to generate structured task lists, draft technical specifications, or recommend sprint planning timelines.\n\n" +
                "Try asking me for:\n" +
                "- *\"Generate a sprint breakdown for a mobile banking application\"*\n" +
                "- *\"Draft a documentation spec for a microservices catalog\"*";
    }
}
