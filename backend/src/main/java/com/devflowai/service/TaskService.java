package com.devflowai.service;

import com.devflowai.dto.request.TaskRequest;
import com.devflowai.dto.response.TaskResponse;
import com.devflowai.entity.Project;
import com.devflowai.entity.Task;
import com.devflowai.entity.User;
import com.devflowai.repository.ProjectRepository;
import com.devflowai.repository.TaskRepository;
import com.devflowai.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;
    private final EmailService emailService;

    @Autowired
    public TaskService(
            TaskRepository taskRepository, 
            ProjectRepository projectRepository, 
            UserRepository userRepository, 
            AuditLogService auditLogService,
            EmailService emailService
    ) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.auditLogService = auditLogService;
        this.emailService = emailService;
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private Project getProjectByIdAndUser(Long projectId, User user) {
        return projectRepository.findByIdAndUser(projectId, user)
                .orElseThrow(() -> new RuntimeException("Project not found or not owned by user"));
    }

    private TaskResponse mapToResponse(Task task) {
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

    public TaskResponse createTask(TaskRequest request, String email) {
        User user = getUserByEmail(email);
        Project project = getProjectByIdAndUser(request.getProjectId(), user);

        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(request.getStatus())
                .priority(request.getPriority())
                .dueDate(request.getDueDate())
                .progress(request.getProgress() != null ? request.getProgress() : 0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .user(user)
                .project(project)
                .build();

        Task savedTask = taskRepository.save(task);
        auditLogService.logAction(user, "TASK_CREATED", "Task created: " + savedTask.getTitle() + " under project ID: " + savedTask.getProject().getId());
        emailService.sendEmail(user.getEmail(), "New Task Created: " + savedTask.getTitle(), 
                "Hello " + user.getFullName() + ",\n\nA new task has been created under project: " + project.getTitle() + ".\n\nTask details:\nTitle: " + savedTask.getTitle() + "\nPriority: " + savedTask.getPriority() + "\n\nBest regards,\nDevFlow AI Team");
        return mapToResponse(savedTask);
    }

    public List<TaskResponse> getAllTasks(String email) {
        User user = getUserByEmail(email);
        return taskRepository.findByUser(user).stream()
                .map(this::mapToResponse)
                .toList();
    }

    public TaskResponse getTaskById(Long id, String email) {
        User user = getUserByEmail(email);
        Task task = taskRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        return mapToResponse(task);
    }

    public List<TaskResponse> getTasksByProjectId(Long projectId, String email) {
        User user = getUserByEmail(email);
        Project project = getProjectByIdAndUser(projectId, user);
        return taskRepository.findByProjectAndUser(project, user).stream()
                .map(this::mapToResponse)
                .toList();
    }

    public TaskResponse updateTask(Long id, TaskRequest request, String email) {
        User user = getUserByEmail(email);
        Task task = taskRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        Project project = getProjectByIdAndUser(request.getProjectId(), user);

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());
        task.setPriority(request.getPriority());
        task.setDueDate(request.getDueDate());
        task.setProgress(request.getProgress() != null ? request.getProgress() : 0);
        task.setProject(project);
        task.setUpdatedAt(LocalDateTime.now());

        Task savedTask = taskRepository.save(task);
        auditLogService.logAction(user, "TASK_UPDATED", "Task updated: " + savedTask.getTitle() + ", Status: " + savedTask.getStatus());
        emailService.sendEmail(user.getEmail(), "Task Update: " + savedTask.getTitle(), 
                "Hello " + user.getFullName() + ",\n\nYour task status has been updated to: " + savedTask.getStatus() + ".\n\nBest regards,\nDevFlow AI Team");
        return mapToResponse(savedTask);
    }

    public void deleteTask(Long id, String email) {
        User user = getUserByEmail(email);
        Task task = taskRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        taskRepository.delete(task);
        auditLogService.logAction(user, "TASK_DELETED", "Task deleted: " + task.getTitle() + " (ID: " + id + ")");
    }
}
