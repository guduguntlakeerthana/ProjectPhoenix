package com.devflowai.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ProjectResponse {

    private Long id;
    private String title;
    private String description;
    private String techStack;
    private String status;
    private LocalDate startDate;
    private LocalDate endDate;
    private String githubLink;
    private String liveDemoLink;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}