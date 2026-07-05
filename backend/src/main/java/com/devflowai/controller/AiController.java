package com.devflowai.controller;

import com.devflowai.dto.request.AiChatRequest;
import com.devflowai.dto.response.AiChatResponse;
import com.devflowai.service.AiService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "http://localhost:4300")
public class AiController {

    private final AiService aiService;

    @Autowired
    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/chat")
    public AiChatResponse askAi(@Valid @RequestBody AiChatRequest request) {
        return aiService.generateResponse(request.getPrompt());
    }
}
