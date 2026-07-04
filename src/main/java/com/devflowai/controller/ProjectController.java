package com.devflowai.controller;

import com.devflowai.dto.request.ProjectRequest;
import com.devflowai.dto.response.ProjectResponse;
import com.devflowai.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@CrossOrigin(origins = "http://localhost:4300")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @PostMapping
    public ProjectResponse createProject(
            @Valid @RequestBody ProjectRequest request,
            Authentication authentication
    ) {
        return projectService.createProject(request, authentication.getName());
    }

    @GetMapping
    public List<ProjectResponse> getMyProjects(Authentication authentication) {
        return projectService.getMyProjects(authentication.getName());
    }

    @GetMapping("/{id}")
    public ProjectResponse getProjectById(
            @PathVariable Long id,
            Authentication authentication
    ) {
        return projectService.getProjectById(id, authentication.getName());
    }

    @PutMapping("/{id}")
    public ProjectResponse updateProject(
            @PathVariable Long id,
            @Valid @RequestBody ProjectRequest request,
            Authentication authentication
    ) {
        return projectService.updateProject(id, request, authentication.getName());
    }

    @DeleteMapping("/{id}")
    public String deleteProject(
            @PathVariable Long id,
            Authentication authentication
    ) {
        projectService.deleteProject(id, authentication.getName());
        return "Project deleted successfully";
    }
}