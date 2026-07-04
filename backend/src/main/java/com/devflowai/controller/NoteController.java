package com.devflowai.controller;

import com.devflowai.dto.request.NoteRequest;
import com.devflowai.dto.response.NoteResponse;
import com.devflowai.service.NoteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
@CrossOrigin(origins = "http://localhost:4300")
public class NoteController {

    private final NoteService noteService;

    @Autowired
    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @PostMapping
    public NoteResponse createNote(
            @Valid @RequestBody NoteRequest request,
            Authentication authentication
    ) {
        return noteService.createNote(request, authentication.getName());
    }

    @GetMapping
    public List<NoteResponse> getAllNotes(Authentication authentication) {
        return noteService.getAllNotes(authentication.getName());
    }

    @GetMapping("/{id}")
    public NoteResponse getNoteById(
            @PathVariable Long id,
            Authentication authentication
    ) {
        return noteService.getNoteById(id, authentication.getName());
    }

    @GetMapping("/project/{projectId}")
    public List<NoteResponse> getNotesByProjectId(
            @PathVariable Long projectId,
            Authentication authentication
    ) {
        return noteService.getNotesByProjectId(projectId, authentication.getName());
    }

    @PutMapping("/{id}")
    public NoteResponse updateNote(
            @PathVariable Long id,
            @Valid @RequestBody NoteRequest request,
            Authentication authentication
    ) {
        return noteService.updateNote(id, request, authentication.getName());
    }

    @DeleteMapping("/{id}")
    public String deleteNote(
            @PathVariable Long id,
            Authentication authentication
    ) {
        noteService.deleteNote(id, authentication.getName());
        return "Note deleted successfully";
    }
}
