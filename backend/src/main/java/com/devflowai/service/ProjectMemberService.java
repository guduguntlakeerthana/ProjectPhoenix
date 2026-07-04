package com.devflowai.service;

import com.devflowai.dto.request.ProjectMemberRequest;
import com.devflowai.dto.response.ProjectMemberResponse;
import com.devflowai.entity.Project;
import com.devflowai.entity.ProjectMember;
import com.devflowai.entity.User;
import com.devflowai.repository.ProjectMemberRepository;
import com.devflowai.repository.ProjectRepository;
import com.devflowai.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProjectMemberService {

    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Autowired
    public ProjectMemberService(ProjectMemberRepository projectMemberRepository, ProjectRepository projectRepository, UserRepository userRepository, NotificationService notificationService) {
        this.projectMemberRepository = projectMemberRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private Project getProjectByIdAndOwner(Long projectId, String ownerEmail) {
        User owner = getUserByEmail(ownerEmail);
        return projectRepository.findByIdAndUser(projectId, owner)
                .orElseThrow(() -> new RuntimeException("Project not found or not owned by current user"));
    }

    private ProjectMemberResponse mapToResponse(ProjectMember member) {
        return ProjectMemberResponse.builder()
                .id(member.getId())
                .email(member.getEmail())
                .role(member.getRole())
                .projectId(member.getProject().getId())
                .projectTitle(member.getProject().getTitle())
                .invitedAt(member.getInvitedAt())
                .build();
    }

    public ProjectMemberResponse inviteMember(ProjectMemberRequest request, String ownerEmail) {
        Project project = getProjectByIdAndOwner(request.getProjectId(), ownerEmail);

        // Check if member user exists in registration database (standard validation)
        User invitedUser = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invited user does not have a DevFlow AI account yet"));

        // Prevent inviting the owner
        if (project.getUser().getEmail().equalsIgnoreCase(request.getEmail())) {
            throw new RuntimeException("The project creator is already the owner");
        }

        // Check if already invited
        if (projectMemberRepository.findByProjectAndEmail(project, request.getEmail()).isPresent()) {
            throw new RuntimeException("User is already a member of this project");
        }

        ProjectMember member = ProjectMember.builder()
                .email(request.getEmail().toLowerCase())
                .role(request.getRole())
                .invitedAt(LocalDateTime.now())
                .project(project)
                .build();

        ProjectMemberResponse response = mapToResponse(projectMemberRepository.save(member));
        notificationService.createNotification(invitedUser, "You have been invited to collaborate on project: " + project.getTitle());
        return response;
    }

    public List<ProjectMemberResponse> getProjectMembers(Long projectId, String ownerEmail) {
        Project project = getProjectByIdAndOwner(projectId, ownerEmail);
        return projectMemberRepository.findByProject(project).stream()
                .map(this::mapToResponse)
                .toList();
    }

    public void removeMember(Long memberId, String ownerEmail) {
        ProjectMember member = projectMemberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member record not found"));
        // Ensure owner owns the project
        getProjectByIdAndOwner(member.getProject().getId(), ownerEmail);

        projectMemberRepository.delete(member);
    }
}
