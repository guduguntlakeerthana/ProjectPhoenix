package com.devflowai.service;

import com.devflowai.dto.request.NoteRequest;
import com.devflowai.dto.response.NoteResponse;
import com.devflowai.entity.Note;
import com.devflowai.entity.Project;
import com.devflowai.entity.User;
import com.devflowai.repository.NoteRepository;
import com.devflowai.repository.ProjectRepository;
import com.devflowai.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class NoteService {

    private final NoteRepository noteRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    @Autowired
    public NoteService(NoteRepository noteRepository, ProjectRepository projectRepository, UserRepository userRepository) {
        this.noteRepository = noteRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private Project getProjectByIdAndUser(Long projectId, User user) {
        if (projectId == null) return null;
        return projectRepository.findByIdAndUser(projectId, user)
                .orElseThrow(() -> new RuntimeException("Project not found or not owned by user"));
    }

    private NoteResponse mapToResponse(Note note) {
        return NoteResponse.builder()
                .id(note.getId())
                .title(note.getTitle())
                .content(note.getContent())
                .projectId(note.getProject() != null ? note.getProject().getId() : null)
                .projectTitle(note.getProject() != null ? note.getProject().getTitle() : null)
                .createdAt(note.getCreatedAt())
                .updatedAt(note.getUpdatedAt())
                .build();
    }

    public NoteResponse createNote(NoteRequest request, String email) {
        User user = getUserByEmail(email);
        Project project = getProjectByIdAndUser(request.getProjectId(), user);

        Note note = Note.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .user(user)
                .project(project)
                .build();

        return mapToResponse(noteRepository.save(note));
    }

    public List<NoteResponse> getAllNotes(String email) {
        User user = getUserByEmail(email);
        return noteRepository.findByUser(user).stream()
                .map(this::mapToResponse)
                .toList();
    }

    public NoteResponse getNoteById(Long id, String email) {
        User user = getUserByEmail(email);
        Note note = noteRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Note not found"));
        return mapToResponse(note);
    }

    public List<NoteResponse> getNotesByProjectId(Long projectId, String email) {
        User user = getUserByEmail(email);
        Project project = getProjectByIdAndUser(projectId, user);
        return noteRepository.findByProjectAndUser(project, user).stream()
                .map(this::mapToResponse)
                .toList();
    }

    public NoteResponse updateNote(Long id, NoteRequest request, String email) {
        User user = getUserByEmail(email);
        Note note = noteRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Note not found"));
        Project project = getProjectByIdAndUser(request.getProjectId(), user);

        note.setTitle(request.getTitle());
        note.setContent(request.getContent());
        note.setProject(project);
        note.setUpdatedAt(LocalDateTime.now());

        return mapToResponse(noteRepository.save(note));
    }

    public void deleteNote(Long id, String email) {
        User user = getUserByEmail(email);
        Note note = noteRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Note not found"));
        noteRepository.delete(note);
    }
}
