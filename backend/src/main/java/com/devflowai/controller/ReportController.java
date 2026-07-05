package com.devflowai.controller;

import com.devflowai.dto.response.ReportResponse;
import com.devflowai.entity.Project;
import com.devflowai.entity.Task;
import com.devflowai.entity.User;
import com.devflowai.repository.ProjectRepository;
import com.devflowai.repository.TaskRepository;
import com.devflowai.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "http://localhost:4300")
public class ReportController {

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Autowired
    public ReportController(
            ProjectRepository projectRepository,
            TaskRepository taskRepository,
            UserRepository userRepository
    ) {
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @GetMapping("/summary")
    public ReportResponse getWorkspaceSummary(Authentication authentication) {
        User user = getUserByEmail(authentication.getName());

        List<Project> projects = projectRepository.findByUser(user);
        List<Task> tasks = taskRepository.findByUser(user);

        long completedTasks = tasks.stream().filter(t -> "DONE".equalsIgnoreCase(t.getStatus())).count();
        long inProgressTasks = tasks.stream().filter(t -> "IN_PROGRESS".equalsIgnoreCase(t.getStatus())).count();
        long reviewTasks = tasks.stream().filter(t -> "REVIEW".equalsIgnoreCase(t.getStatus())).count();
        long todoTasks = tasks.stream().filter(t -> "TODO".equalsIgnoreCase(t.getStatus())).count();

        double averageProgress = tasks.isEmpty() ? 0.0 :
                tasks.stream().mapToInt(t -> t.getProgress() != null ? t.getProgress() : 0).average().orElse(0.0);

        long highPriority = tasks.stream().filter(t -> "HIGH".equalsIgnoreCase(t.getPriority())).count();
        long mediumPriority = tasks.stream().filter(t -> "MEDIUM".equalsIgnoreCase(t.getPriority())).count();
        long lowPriority = tasks.stream().filter(t -> "LOW".equalsIgnoreCase(t.getPriority())).count();

        return ReportResponse.builder()
                .totalProjects(projects.size())
                .totalTasks(tasks.size())
                .completedTasks(completedTasks)
                .inProgressTasks(inProgressTasks)
                .reviewTasks(reviewTasks)
                .todoTasks(todoTasks)
                .averageTaskProgress(averageProgress)
                .highPriorityTasks(highPriority)
                .mediumPriorityTasks(mediumPriority)
                .lowPriorityTasks(lowPriority)
                .build();
    }
}
