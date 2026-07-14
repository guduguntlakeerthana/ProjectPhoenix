package com.devflowai.controller;

import com.devflowai.dto.request.ProjectRequest;
import com.devflowai.dto.response.ProjectResponse;
import com.devflowai.dto.response.ProjectStatsResponse;
import com.devflowai.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    @GetMapping("/all")
    public List<ProjectResponse> getAllProjects(Authentication authentication) {
        return projectService.getMyProjects(authentication.getName());
    }

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
    public Page<ProjectResponse> getMyProjects(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            Authentication authentication
    ) {
        return projectService.getMyProjectsPaginated(
                authentication.getName(), search, status, page, size, sortBy, direction
        );
    }

    @GetMapping("/stats")
    public ProjectStatsResponse getProjectStats(Authentication authentication) {
        return projectService.getProjectStats(authentication.getName());
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