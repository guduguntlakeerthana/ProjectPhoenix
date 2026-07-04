package com.devflowai.service;

import com.devflowai.dto.request.ProjectRequest;
import com.devflowai.dto.response.ProjectResponse;
import com.devflowai.entity.Project;
import com.devflowai.entity.User;
import com.devflowai.repository.ProjectRepository;
import com.devflowai.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

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

    public ProjectResponse createProject(ProjectRequest request, String email) {
        User user = getUserByEmail(email);

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

        return mapToResponse(projectRepository.save(project));
    }

    public List<ProjectResponse> getMyProjects(String email) {
        User user = getUserByEmail(email);

        return projectRepository.findByUser(user)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public ProjectResponse getProjectById(Long id, String email) {
        User user = getUserByEmail(email);

        Project project = projectRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        return mapToResponse(project);
    }

    public ProjectResponse updateProject(Long id, ProjectRequest request, String email) {
        User user = getUserByEmail(email);

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

        return mapToResponse(projectRepository.save(project));
    }

    public void deleteProject(Long id, String email) {
        User user = getUserByEmail(email);

        Project project = projectRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        projectRepository.delete(project);
    }
}