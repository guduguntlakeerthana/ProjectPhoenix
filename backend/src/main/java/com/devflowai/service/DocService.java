package com.devflowai.service;

import com.devflowai.dto.request.DocRequest;
import com.devflowai.dto.response.DocResponse;
import com.devflowai.entity.Doc;
import com.devflowai.entity.Project;
import com.devflowai.entity.User;
import com.devflowai.repository.DocRepository;
import com.devflowai.repository.ProjectRepository;
import com.devflowai.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class DocService {

    private final DocRepository docRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    @Autowired
    public DocService(DocRepository docRepository, ProjectRepository projectRepository, UserRepository userRepository) {
        this.docRepository = docRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private Project getProjectByIdAndUser(Long projectId, User user) {
        return projectRepository.findByIdAndUser(projectId, user)
                .orElseThrow(() -> new RuntimeException("Project not found or not owned by user"));
    }

    private DocResponse mapToResponse(Doc doc) {
        return DocResponse.builder()
                .id(doc.getId())
                .title(doc.getTitle())
                .content(doc.getContent())
                .category(doc.getCategory())
                .projectId(doc.getProject().getId())
                .projectTitle(doc.getProject().getTitle())
                .createdAt(doc.getCreatedAt())
                .updatedAt(doc.getUpdatedAt())
                .build();
    }

    public DocResponse createDoc(DocRequest request, String email) {
        User user = getUserByEmail(email);
        Project project = getProjectByIdAndUser(request.getProjectId(), user);

        Doc doc = Doc.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .category(request.getCategory())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .user(user)
                .project(project)
                .build();

        return mapToResponse(docRepository.save(doc));
    }

    public List<DocResponse> getAllDocs(String email) {
        User user = getUserByEmail(email);
        return docRepository.findByUser(user).stream()
                .map(this::mapToResponse)
                .toList();
    }

    public DocResponse getDocById(Long id, String email) {
        User user = getUserByEmail(email);
        Doc doc = docRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Documentation page not found"));
        return mapToResponse(doc);
    }

    public List<DocResponse> getDocsByProjectId(Long projectId, String email) {
        User user = getUserByEmail(email);
        Project project = getProjectByIdAndUser(projectId, user);
        return docRepository.findByProjectAndUser(project, user).stream()
                .map(this::mapToResponse)
                .toList();
    }

    public DocResponse updateDoc(Long id, DocRequest request, String email) {
        User user = getUserByEmail(email);
        Doc doc = docRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Documentation page not found"));
        Project project = getProjectByIdAndUser(request.getProjectId(), user);

        doc.setTitle(request.getTitle());
        doc.setContent(request.getContent());
        doc.setCategory(request.getCategory());
        doc.setProject(project);
        doc.setUpdatedAt(LocalDateTime.now());

        return mapToResponse(docRepository.save(doc));
    }

    public void deleteDoc(Long id, String email) {
        User user = getUserByEmail(email);
        Doc doc = docRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Documentation page not found"));
        docRepository.delete(doc);
    }
}
