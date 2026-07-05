package com.devflowai.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TaskRequest {

    @NotBlank(message = "Task title is required")
    private String title;

    private String description;

    @NotBlank(message = "Status is required")
    @Pattern(regexp = "^(TODO|IN_PROGRESS|REVIEW|DONE)$", message = "Status must be TODO, IN_PROGRESS, REVIEW, or DONE")
    private String status;

    @NotBlank(message = "Priority is required")
    @Pattern(regexp = "^(LOW|MEDIUM|HIGH)$", message = "Priority must be LOW, MEDIUM, or HIGH")
    private String priority;

    private LocalDate dueDate;

    @Min(value = 0, message = "Progress cannot be less than 0")
    @Max(value = 100, message = "Progress cannot exceed 100")
    private Integer progress;

    @NotNull(message = "Project ID is required")
    private Long projectId;
}
