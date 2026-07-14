package com.devflowai.service;

import com.devflowai.dto.request.ProjectRequest;
import com.devflowai.dto.response.ProjectResponse;
import com.devflowai.entity.Project;
import com.devflowai.entity.User;
import com.devflowai.repository.ProjectRepository;
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
public class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private ProjectService projectService;

    @Test
    public void testCreateProjectSuccess() {
        String email = "keerthana@devflowai.com";
        User user = User.builder().id(1L).email(email).build();

        ProjectRequest request = new ProjectRequest();
        request.setTitle("Project Phoenix");
        request.setDescription("Agile AI Workspace");
        request.setTechStack("Angular, Spring Boot");
        request.setStatus("IN_PROGRESS");
        request.setStartDate(LocalDate.now());
        request.setEndDate(LocalDate.now().plusMonths(3));

        Project project = Project.builder()
                .id(100L)
                .title(request.getTitle())
                .description(request.getDescription())
                .techStack(request.getTechStack())
                .status(request.getStatus())
                .user(user)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        ProjectResponse response = projectService.createProject(request, email);

        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals("Project Phoenix", response.getTitle());
        verify(projectRepository, times(1)).save(any(Project.class));
        verify(auditLogService, times(1)).logAction(eq(user), eq("PROJECT_CREATED"), anyString());
    }

    @Test
    public void testGetProjectByIdAndUserSuccess() {
        String email = "keerthana@devflowai.com";
        User user = User.builder().id(1L).email(email).build();
        Project project = Project.builder().id(100L).title("Project Phoenix").user(user).build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(projectRepository.findByIdAndUser(100L, user)).thenReturn(Optional.of(project));

        ProjectResponse response = projectService.getProjectById(100L, email);

        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals("Project Phoenix", response.getTitle());
    }
}
