package com.devflowai.controller;

import com.devflowai.dto.request.TaskRequest;
import com.devflowai.dto.response.TaskResponse;
import com.devflowai.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "http://localhost:4300")
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public TaskResponse createTask(
            @Valid @RequestBody TaskRequest request,
            Authentication authentication
    ) {
        return taskService.createTask(request, authentication.getName());
    }

    @GetMapping
    public List<TaskResponse> getAllTasks(Authentication authentication) {
        return taskService.getAllTasks(authentication.getName());
    }

    @GetMapping("/{id}")
    public TaskResponse getTaskById(
            @PathVariable Long id,
            Authentication authentication
    ) {
        return taskService.getTaskById(id, authentication.getName());
    }

    @GetMapping("/project/{projectId}")
    public List<TaskResponse> getTasksByProjectId(
            @PathVariable Long projectId,
            Authentication authentication
    ) {
        return taskService.getTasksByProjectId(projectId, authentication.getName());
    }

    @PutMapping("/{id}")
    public TaskResponse updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskRequest request,
            Authentication authentication
    ) {
        return taskService.updateTask(id, request, authentication.getName());
    }

    @DeleteMapping("/{id}")
    public String deleteTask(
            @PathVariable Long id,
            Authentication authentication
    ) {
        taskService.deleteTask(id, authentication.getName());
        return "Task deleted successfully";
    }
}
