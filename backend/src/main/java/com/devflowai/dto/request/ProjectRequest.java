package com.devflowai.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ProjectRequest {

    @NotBlank(message = "Project title is required")
    @Size(max = 100, message = "Project title cannot exceed 100 characters")
    private String title;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @Size(max = 255, message = "Tech stack description cannot exceed 255 characters")
    private String techStack;

    @NotBlank(message = "Status is required")
    @Pattern(regexp = "^(PENDING|IN_PROGRESS|COMPLETED)$", message = "Status must be PENDING, IN_PROGRESS, or COMPLETED")
    private String status;

    private LocalDate startDate;

    private LocalDate endDate;

    private String githubLink;

    private String liveDemoLink;
}