package com.devflowai.controller;

import com.devflowai.dto.response.AdminStatsResponse;
import com.devflowai.dto.response.AdminUserResponse;
import com.devflowai.dto.response.AuditLogResponse;
import com.devflowai.entity.User;
import com.devflowai.repository.AttachmentRepository;
import com.devflowai.repository.ProjectRepository;
import com.devflowai.repository.TaskRepository;
import com.devflowai.repository.UserRepository;
import com.devflowai.service.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final AttachmentRepository attachmentRepository;
    private final AuditLogService auditLogService;

    @Autowired
    public AdminController(
            UserRepository userRepository,
            ProjectRepository projectRepository,
            TaskRepository taskRepository,
            AttachmentRepository attachmentRepository,
            AuditLogService auditLogService
    ) {
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
        this.attachmentRepository = attachmentRepository;
        this.auditLogService = auditLogService;
    }

    @GetMapping("/users")
    public List<AdminUserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(u -> AdminUserResponse.builder()
                        .id(u.getId())
                        .fullName(u.getFullName())
                        .email(u.getEmail())
                        .role(u.getRole())
                        .createdAt(u.getCreatedAt())
                        .build())
                .toList();
    }

    @PutMapping("/users/{id}/role")
    public ResponseEntity<AdminUserResponse> updateUserRole(
            @PathVariable Long id,
            @RequestParam String role,
            Authentication authentication
    ) {
        User targetUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Retrieve actor details for audit logging
        User adminActor = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Admin actor not found"));

        targetUser.setRole(role.toUpperCase());
        User updated = userRepository.save(targetUser);

        auditLogService.logAction(adminActor, "ADMIN_ROLE_CHANGE", 
                "Updated user email " + targetUser.getEmail() + " role to " + role.toUpperCase());

        return ResponseEntity.ok(AdminUserResponse.builder()
                .id(updated.getId())
                .fullName(updated.getFullName())
                .email(updated.getEmail())
                .role(updated.getRole())
                .createdAt(updated.getCreatedAt())
                .build());
    }

    @GetMapping("/stats")
    public AdminStatsResponse getGlobalStats() {
        long usersCount = userRepository.count();
        long projectsCount = projectRepository.count();
        long tasksCount = taskRepository.count();
        long attachmentsCount = attachmentRepository.count();

        return AdminStatsResponse.builder()
                .totalUsers(usersCount)
                .totalProjects(projectsCount)
                .totalTasks(tasksCount)
                .totalAttachments(attachmentsCount)
                .build();
    }

    @GetMapping("/audit-logs")
    public List<AuditLogResponse> getGlobalAuditLogs() {
        return auditLogService.getAllLogs();
    }
}
