package com.devflowai.service;

import com.devflowai.dto.response.*;
import com.devflowai.entity.*;
import com.devflowai.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class GlobalSearchService {

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final NoteRepository noteRepository;
    private final DocRepository docRepository;
    private final UserRepository userRepository;

    @Autowired
    public GlobalSearchService(
            ProjectRepository projectRepository,
            TaskRepository taskRepository,
            NoteRepository noteRepository,
            DocRepository docRepository,
            UserRepository userRepository
    ) {
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
        this.noteRepository = noteRepository;
        this.docRepository = docRepository;
        this.userRepository = userRepository;
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private boolean matches(String value, String q) {
        if (value == null || q == null) return false;
        return value.toLowerCase().contains(q.toLowerCase());
    }

    private ProjectResponse mapProject(Project project) {
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

    private TaskResponse mapTask(Task task) {
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

    private NoteResponse mapNote(Note note) {
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

    private DocResponse mapDoc(Doc doc) {
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

    public GlobalSearchResponse search(String q, String email) {
        if (q == null || q.trim().isEmpty()) {
            return GlobalSearchResponse.builder()
                    .projects(Collections.emptyList())
                    .tasks(Collections.emptyList())
                    .notes(Collections.emptyList())
                    .docs(Collections.emptyList())
                    .build();
        }

        String keyword = q.trim();
        User user = getUserByEmail(email);

        List<ProjectResponse> projects = projectRepository.findByUser(user).stream()
                .filter(p -> matches(p.getTitle(), keyword) || matches(p.getDescription(), keyword) || matches(p.getTechStack(), keyword))
                .map(this::mapProject)
                .toList();

        List<TaskResponse> tasks = taskRepository.findByUser(user).stream()
                .filter(t -> matches(t.getTitle(), keyword) || matches(t.getDescription(), keyword))
                .map(this::mapTask)
                .toList();

        List<NoteResponse> notes = noteRepository.findByUser(user).stream()
                .filter(n -> matches(n.getTitle(), keyword) || matches(n.getContent(), keyword))
                .map(this::mapNote)
                .toList();

        List<DocResponse> docs = docRepository.findByUser(user).stream()
                .filter(d -> matches(d.getTitle(), keyword) || matches(d.getContent(), keyword))
                .map(this::mapDoc)
                .toList();

        return GlobalSearchResponse.builder()
                .projects(projects)
                .tasks(tasks)
                .notes(notes)
                .docs(docs)
                .build();
    }
}
