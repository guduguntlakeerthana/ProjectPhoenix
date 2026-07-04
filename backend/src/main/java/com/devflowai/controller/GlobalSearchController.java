package com.devflowai.controller;

import com.devflowai.dto.response.GlobalSearchResponse;
import com.devflowai.service.GlobalSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/search")
@CrossOrigin(origins = "http://localhost:4300")
public class GlobalSearchController {

    private final GlobalSearchService globalSearchService;

    @Autowired
    public GlobalSearchController(GlobalSearchService globalSearchService) {
        this.globalSearchService = globalSearchService;
    }

    @GetMapping
    public GlobalSearchResponse search(
            @RequestParam String q,
            Authentication authentication
    ) {
        return globalSearchService.search(q, authentication.getName());
    }
}
