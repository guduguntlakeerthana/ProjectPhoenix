package com.devflowai.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String content;

    @NotBlank(message = "Category is required")
    @Pattern(regexp = "^(SETUP|API|ARCH|OTHER)$", message = "Category must be SETUP, API, ARCH, or OTHER")
    private String category;

    @NotNull(message = "Project ID is required")
    private Long projectId;
}
