package com.devflowai.controller;

import com.devflowai.dto.response.AnalyticsResponse;
import com.devflowai.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "http://localhost:4300")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @Autowired
    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping
    public AnalyticsResponse getAnalytics(Authentication authentication) {
        return analyticsService.getAnalytics(authentication.getName());
    }
}
