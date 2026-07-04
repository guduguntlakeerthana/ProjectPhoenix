package com.devflowai.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ProjectRequest {

    @NotBlank(message = "Project title is required")
    private String title;

    private String description;

    private String techStack;

    private String status;

    private LocalDate startDate;

    private LocalDate endDate;

    private String githubLink;

    private String liveDemoLink;
}