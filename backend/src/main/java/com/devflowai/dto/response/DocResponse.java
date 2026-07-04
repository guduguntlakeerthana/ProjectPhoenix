package com.devflowai.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class DocResponse {
    private Long id;
    private String title;
    private String content;
    private String category;
    private Long projectId;
    private String projectTitle;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
