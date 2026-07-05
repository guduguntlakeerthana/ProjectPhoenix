package com.devflowai.service;

import com.devflowai.dto.response.AttachmentResponse;
import com.devflowai.entity.Attachment;
import com.devflowai.entity.Project;
import com.devflowai.entity.Task;
import com.devflowai.entity.User;
import com.devflowai.repository.AttachmentRepository;
import com.devflowai.repository.ProjectMemberRepository;
import com.devflowai.repository.ProjectRepository;
import com.devflowai.repository.TaskRepository;
import com.devflowai.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final Path uploadLocation;

    @Autowired
    public AttachmentService(
            AttachmentRepository attachmentRepository,
            ProjectRepository projectRepository,
            TaskRepository taskRepository,
            UserRepository userRepository,
            ProjectMemberRepository projectMemberRepository
    ) {
        this.attachmentRepository = attachmentRepository;
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.projectMemberRepository = projectMemberRepository;

        // Establish safe uploads directory
        String userDir = System.getProperty("user.dir");
        this.uploadLocation = Paths.get(userDir, "uploads").toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage directory", e);
        }
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private void checkProjectAccess(Project project, String userEmail) {
        boolean isOwner = project.getUser().getEmail().equalsIgnoreCase(userEmail);
        boolean isMember = projectMemberRepository.findByProjectAndEmail(project, userEmail).isPresent();
        if (!isOwner && !isMember) {
            throw new RuntimeException("Access Denied: You are not a collaborator of this project");
        }
    }

    private AttachmentResponse mapToResponse(Attachment attachment) {
        return AttachmentResponse.builder()
                .id(attachment.getId())
                .fileName(attachment.getFileName())
                .fileType(attachment.getFileType())
                .fileSize(attachment.getFileSize())
                .projectId(attachment.getProject() != null ? attachment.getProject().getId() : null)
                .taskId(attachment.getTask() != null ? attachment.getTask().getId() : null)
                .createdAt(attachment.getCreatedAt())
                .build();
    }

    public AttachmentResponse uploadFile(MultipartFile file, Long projectId, Long taskId, String userEmail) {
        User user = getUserByEmail(userEmail);
        Project project = null;
        Task task = null;

        if (projectId != null) {
            project = projectRepository.findById(projectId)
                    .orElseThrow(() -> new RuntimeException("Project not found"));
            checkProjectAccess(project, userEmail);
        }

        if (taskId != null) {
            task = taskRepository.findById(taskId)
                    .orElseThrow(() -> new RuntimeException("Task not found"));
            if (project == null) {
                project = task.getProject();
            }
            checkProjectAccess(project, userEmail);
        }

        // Clean file name to prevent traversal attacks
        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null) {
            throw new RuntimeException("Invalid file name");
        }
        String cleanFileName = Paths.get(originalFileName).getFileName().toString();

        // Unique store name
        String storedFileName = UUID.randomUUID().toString() + "_" + cleanFileName;
        Path targetPath = this.uploadLocation.resolve(storedFileName);

        try {
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Could not store file: " + cleanFileName, e);
        }

        Attachment attachment = Attachment.builder()
                .fileName(cleanFileName)
                .fileType(file.getContentType())
                .fileSize(file.getSize())
                .filePath(targetPath.toString())
                .project(project)
                .task(task)
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();

        return mapToResponse(attachmentRepository.save(attachment));
    }

    public Resource downloadFile(Long id, String userEmail) {
        Attachment attachment = attachmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Attachment not found"));

        // Verify access rights
        if (attachment.getProject() != null) {
            checkProjectAccess(attachment.getProject(), userEmail);
        } else if (!attachment.getUser().getEmail().equalsIgnoreCase(userEmail)) {
            throw new RuntimeException("Access Denied: You do not own this attachment");
        }

        try {
            Path filePath = Paths.get(attachment.getFilePath()).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("File not found or not readable: " + attachment.getFileName());
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Could not read file: " + attachment.getFileName(), e);
        }
    }

    public void deleteFile(Long id, String userEmail) {
        Attachment attachment = attachmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Attachment not found"));

        // Only uploader or project owner can delete files
        boolean isUploader = attachment.getUser().getEmail().equalsIgnoreCase(userEmail);
        boolean isProjectOwner = false;
        if (attachment.getProject() != null) {
            isProjectOwner = attachment.getProject().getUser().getEmail().equalsIgnoreCase(userEmail);
        }

        if (!isUploader && !isProjectOwner) {
            throw new RuntimeException("Access Denied: You cannot delete this attachment");
        }

        // Delete from local storage
        try {
            Path path = Paths.get(attachment.getFilePath());
            Files.deleteIfExists(path);
        } catch (IOException e) {
            System.err.println("Could not delete filesystem record for file: " + attachment.getFileName());
        }

        attachmentRepository.delete(attachment);
    }

    public List<AttachmentResponse> getProjectAttachments(Long projectId, String userEmail) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        checkProjectAccess(project, userEmail);

        return attachmentRepository.findByProject(project).stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<AttachmentResponse> getTaskAttachments(Long taskId, String userEmail) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        checkProjectAccess(task.getProject(), userEmail);

        return attachmentRepository.findByTask(task).stream()
                .map(this::mapToResponse)
                .toList();
    }
}
