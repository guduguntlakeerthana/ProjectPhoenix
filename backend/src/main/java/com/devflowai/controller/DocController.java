package com.devflowai.controller;

import com.devflowai.dto.request.DocRequest;
import com.devflowai.dto.response.DocResponse;
import com.devflowai.service.DocService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/docs")
public class DocController {

    private final DocService docService;

    @Autowired
    public DocController(DocService docService) {
        this.docService = docService;
    }

    @PostMapping
    public DocResponse createDoc(
            @Valid @RequestBody DocRequest request,
            Authentication authentication
    ) {
        return docService.createDoc(request, authentication.getName());
    }

    @GetMapping
    public List<DocResponse> getAllDocs(Authentication authentication) {
        return docService.getAllDocs(authentication.getName());
    }

    @GetMapping("/{id}")
    public DocResponse getDocById(
            @PathVariable Long id,
            Authentication authentication
    ) {
        return docService.getDocById(id, authentication.getName());
    }

    @GetMapping("/project/{projectId}")
    public List<DocResponse> getDocsByProjectId(
            @PathVariable Long projectId,
            Authentication authentication
    ) {
        return docService.getDocsByProjectId(projectId, authentication.getName());
    }

    @PutMapping("/{id}")
    public DocResponse updateDoc(
            @PathVariable Long id,
            @Valid @RequestBody DocRequest request,
            Authentication authentication
    ) {
        return docService.updateDoc(id, request, authentication.getName());
    }

    @DeleteMapping("/{id}")
    public String deleteDoc(
            @PathVariable Long id,
            Authentication authentication
    ) {
        docService.deleteDoc(id, authentication.getName());
        return "Documentation deleted successfully";
    }
}
