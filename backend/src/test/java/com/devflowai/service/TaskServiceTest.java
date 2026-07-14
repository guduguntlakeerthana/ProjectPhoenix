package com.devflowai.service;

import com.devflowai.dto.request.TaskRequest;
import com.devflowai.dto.response.TaskResponse;
import com.devflowai.entity.Project;
import com.devflowai.entity.Task;
import com.devflowai.entity.User;
import com.devflowai.repository.ProjectRepository;
import com.devflowai.repository.TaskRepository;
import com.devflowai.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuditLogService auditLogService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private TaskService taskService;

    @Test
    public void testCreateTaskSuccess() {
        String email = "keerthana@devflowai.com";
        User user = User.builder().id(1L).email(email).fullName("Keerthana Guduguntla").build();
        Project project = Project.builder().id(100L).title("Project Phoenix").user(user).build();

        TaskRequest request = new TaskRequest();
        request.setTitle("Setup API Gateway");
        request.setDescription("Map REST filters");
        request.setStatus("TODO");
        request.setPriority("HIGH");
        request.setDueDate(LocalDate.now());
        request.setProgress(0);
        request.setProjectId(100L);

        Task task = Task.builder()
                .id(200L)
                .title(request.getTitle())
                .description(request.getDescription())
                .status(request.getStatus())
                .priority(request.getPriority())
                .dueDate(request.getDueDate())
                .progress(request.getProgress())
                .project(project)
                .user(user)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(projectRepository.findByIdAndUser(100L, user)).thenReturn(Optional.of(project));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskResponse response = taskService.createTask(request, email);

        assertNotNull(response);
        assertEquals(200L, response.getId());
        assertEquals("Setup API Gateway", response.getTitle());
        assertEquals("Project Phoenix", response.getProjectTitle());

        verify(taskRepository, times(1)).save(any(Task.class));
        verify(emailService, times(1)).sendEmail(eq(email), anyString(), anyString());
    }
}
