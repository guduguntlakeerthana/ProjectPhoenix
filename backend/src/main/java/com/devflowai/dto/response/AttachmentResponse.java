package com.devflowai.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class AttachmentResponse {
    private Long id;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private Long projectId;
    private Long taskId;
    private LocalDateTime createdAt;
}
