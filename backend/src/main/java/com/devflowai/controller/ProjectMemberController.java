package com.devflowai.controller;

import com.devflowai.dto.request.ProjectMemberRequest;
import com.devflowai.dto.response.ProjectMemberResponse;
import com.devflowai.service.ProjectMemberService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/project-members")
@CrossOrigin(origins = "http://localhost:4300")
public class ProjectMemberController {

    private final ProjectMemberService projectMemberService;

    @Autowired
    public ProjectMemberController(ProjectMemberService projectMemberService) {
        this.projectMemberService = projectMemberService;
    }

    @PostMapping
    public ProjectMemberResponse inviteMember(
            @Valid @RequestBody ProjectMemberRequest request,
            Authentication authentication
    ) {
        return projectMemberService.inviteMember(request, authentication.getName());
    }

    @GetMapping("/project/{projectId}")
    public List<ProjectMemberResponse> getProjectMembers(
            @PathVariable Long projectId,
            Authentication authentication
    ) {
        return projectMemberService.getProjectMembers(projectId, authentication.getName());
    }

    @DeleteMapping("/{id}")
    public String removeMember(
            @PathVariable Long id,
            Authentication authentication
    ) {
        projectMemberService.removeMember(id, authentication.getName());
        return "Member removed successfully";
    }
}
