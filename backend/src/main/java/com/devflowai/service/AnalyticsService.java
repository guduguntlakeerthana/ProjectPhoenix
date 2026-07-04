package com.devflowai.service;

import com.devflowai.dto.response.AnalyticsResponse;
import com.devflowai.dto.response.ProjectResponse;
import com.devflowai.dto.response.TaskResponse;
import com.devflowai.entity.Project;
import com.devflowai.entity.Task;
import com.devflowai.entity.User;
import com.devflowai.repository.ProjectRepository;
import com.devflowai.repository.TaskRepository;
import com.devflowai.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnalyticsService {

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Autowired
    public AnalyticsService(ProjectRepository projectRepository, TaskRepository taskRepository, UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private ProjectResponse mapProjectToResponse(Project project) {
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

    private TaskResponse mapTaskToResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .dueDate(task.getDueDate())
                .progress(task.getProgress())
                .projectId(task.getProject().getId())
                .projectTitle(task.getProject().getTitle())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }

    public AnalyticsResponse getAnalytics(String email) {
        User user = getUserByEmail(email);

        List<Project> projects = projectRepository.findByUser(user);
        List<Task> tasks = taskRepository.findByUser(user);

        // Projects statistics
        long totalProjects = projects.size();
        long completedProjects = projects.stream().filter(p -> "COMPLETED".equals(p.getStatus())).count();
        long inProgressProjects = projects.stream().filter(p -> "IN_PROGRESS".equals(p.getStatus())).count();
        long pendingProjects = projects.stream().filter(p -> "PENDING".equals(p.getStatus())).count();

        // Tasks statistics
        long totalTasks = tasks.size();
        long todoTasks = tasks.stream().filter(t -> "TODO".equals(t.getStatus())).count();
        long inProgressTasks = tasks.stream().filter(t -> "IN_PROGRESS".equals(t.getStatus())).count();
        long completedTasks = tasks.stream().filter(t -> "DONE".equals(t.getStatus())).count();

        // Priority breakdown
        long lowPriority = tasks.stream().filter(t -> "LOW".equals(t.getPriority())).count();
        long mediumPriority = tasks.stream().filter(t -> "MEDIUM".equals(t.getPriority())).count();
        long highPriority = tasks.stream().filter(t -> "HIGH".equals(t.getPriority())).count();

        // Recent Projects (up to 3)
        List<ProjectResponse> recentProjects = projects.stream()
                .sorted((a, b) -> {
                    if (a.getCreatedAt() == null) return 1;
                    if (b.getCreatedAt() == null) return -1;
                    return b.getCreatedAt().compareTo(a.getCreatedAt());
                })
                .limit(3)
                .map(this::mapProjectToResponse)
                .toList();

        // Recent Tasks (up to 5)
        List<TaskResponse> recentTasks = tasks.stream()
                .sorted((a, b) -> {
                    if (a.getCreatedAt() == null) return 1;
                    if (b.getCreatedAt() == null) return -1;
                    return b.getCreatedAt().compareTo(a.getCreatedAt());
                })
                .limit(5)
                .map(this::mapTaskToResponse)
                .toList();

        return AnalyticsResponse.builder()
                .totalProjects(totalProjects)
                .completedProjects(completedProjects)
                .inProgressProjects(inProgressProjects)
                .pendingProjects(pendingProjects)
                .totalTasks(totalTasks)
                .todoTasks(todoTasks)
                .inProgressTasks(inProgressTasks)
                .completedTasks(completedTasks)
                .lowPriorityTasks(lowPriority)
                .mediumPriorityTasks(mediumPriority)
                .highPriorityTasks(highPriority)
                .recentProjects(recentProjects)
                .recentTasks(recentTasks)
                .build();
    }
}
