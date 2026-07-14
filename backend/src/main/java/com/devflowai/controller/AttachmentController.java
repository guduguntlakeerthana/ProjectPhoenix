package com.devflowai.controller;

import com.devflowai.dto.response.AttachmentResponse;
import com.devflowai.service.AttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/attachments")
public class AttachmentController {

    private final AttachmentService attachmentService;

    @Autowired
    public AttachmentController(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    @PostMapping("/upload")
    public AttachmentResponse uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "projectId", required = false) Long projectId,
            @RequestParam(value = "taskId", required = false) Long taskId,
            Authentication authentication
    ) {
        return attachmentService.uploadFile(file, projectId, taskId, authentication.getName());
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable Long id,
            Authentication authentication
    ) {
        Resource resource = attachmentService.downloadFile(id, authentication.getName());
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @DeleteMapping("/{id}")
    public String deleteFile(
            @PathVariable Long id,
            Authentication authentication
    ) {
        attachmentService.deleteFile(id, authentication.getName());
        return "File deleted successfully";
    }

    @GetMapping("/project/{projectId}")
    public List<AttachmentResponse> getProjectAttachments(
            @PathVariable Long projectId,
            Authentication authentication
    ) {
        return attachmentService.getProjectAttachments(projectId, authentication.getName());
    }

    @GetMapping("/task/{taskId}")
    public List<AttachmentResponse> getTaskAttachments(
            @PathVariable Long taskId,
            Authentication authentication
    ) {
        return attachmentService.getTaskAttachments(taskId, authentication.getName());
    }
}
