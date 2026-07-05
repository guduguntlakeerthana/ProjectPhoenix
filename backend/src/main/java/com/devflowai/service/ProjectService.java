package com.devflowai.service;

import com.devflowai.dto.request.ProjectRequest;
import com.devflowai.dto.response.ProjectResponse;
import com.devflowai.dto.response.ProjectStatsResponse;
import com.devflowai.entity.Project;
import com.devflowai.entity.User;
import com.devflowai.repository.ProjectRepository;
import com.devflowai.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuditLogService auditLogService;

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private ProjectResponse mapToResponse(Project project) {
        return ProjectResponse.builder()
                .id(project.getId())
                .title(project.getTitle())
                .description(project.getDescription())
                .techStack(project.getTechStack())
                .status(project.getStatus())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .githubLink(project.getGithubLink())
                .liveDemoLink(project.getLiveDemoLink())
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .build();
    }

    private void validateDates(ProjectRequest request) {
        if (request.getStartDate() != null && request.getEndDate() != null) {
            if (request.getStartDate().isAfter(request.getEndDate())) {
                throw new IllegalArgumentException("Start date cannot be after end date");
            }
        }
    }

    public ProjectResponse createProject(ProjectRequest request, String email) {
        User user = getUserByEmail(email);
        validateDates(request);

        Project project = Project.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .techStack(request.getTechStack())
                .status(request.getStatus())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .githubLink(request.getGithubLink())
                .liveDemoLink(request.getLiveDemoLink())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .user(user)
                .build();

        Project savedProject = projectRepository.save(project);
        auditLogService.logAction(user, "PROJECT_CREATED", "Project created: " + savedProject.getTitle() + " (ID: " + savedProject.getId() + ")");
        return mapToResponse(savedProject);
    }

    public List<ProjectResponse> getMyProjects(String email) {
        User user = getUserByEmail(email);

        return projectRepository.findByUser(user)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public Page<ProjectResponse> getMyProjectsPaginated(
            String email,
            String search,
            String status,
            int page,
            int size,
            String sortBy,
            String direction
    ) {
        User user = getUserByEmail(email);

        // Map client filter 'ALL' to null
        String statusFilter = (status == null || "ALL".equalsIgnoreCase(status)) ? null : status;
        String searchQuery = (search == null || search.trim().isEmpty()) ? null : search.trim();

        // Sort configuration
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        return projectRepository.findByUserAndFilters(user, statusFilter, searchQuery, pageable)
                .map(this::mapToResponse);
    }

    public ProjectStatsResponse getProjectStats(String email) {
        User user = getUserByEmail(email);
        List<Project> projects = projectRepository.findByUser(user);

        long total = projects.size();
        long completed = projects.stream().filter(p -> "COMPLETED".equals(p.getStatus())).count();
        long inProgress = projects.stream().filter(p -> "IN_PROGRESS".equals(p.getStatus())).count();
        long pending = projects.stream().filter(p -> "PENDING".equals(p.getStatus())).count();

        return ProjectStatsResponse.builder()
                .totalProjects(total)
                .completedProjects(completed)
                .inProgressProjects(inProgress)
                .pendingProjects(pending)
                .build();
    }

    public ProjectResponse getProjectById(Long id, String email) {
        User user = getUserByEmail(email);

        Project project = projectRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        return mapToResponse(project);
    }

    public ProjectResponse updateProject(Long id, ProjectRequest request, String email) {
        User user = getUserByEmail(email);
        validateDates(request);

        Project project = projectRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        project.setTitle(request.getTitle());
        project.setDescription(request.getDescription());
        project.setTechStack(request.getTechStack());
        project.setStatus(request.getStatus());
        project.setStartDate(request.getStartDate());
        project.setEndDate(request.getEndDate());
        project.setGithubLink(request.getGithubLink());
        project.setLiveDemoLink(request.getLiveDemoLink());
        project.setUpdatedAt(LocalDateTime.now());

        Project savedProject = projectRepository.save(project);
        auditLogService.logAction(user, "PROJECT_UPDATED", "Project updated: " + savedProject.getTitle() + " (ID: " + savedProject.getId() + ")");
        return mapToResponse(savedProject);
    }

    public void deleteProject(Long id, String email) {
        User user = getUserByEmail(email);

        Project project = projectRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        projectRepository.delete(project);
        auditLogService.logAction(user, "PROJECT_DELETED", "Project deleted: " + project.getTitle() + " (ID: " + id + ")");
    }
}