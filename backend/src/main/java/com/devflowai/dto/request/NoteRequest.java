package com.devflowai.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoteRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String content;

    private Long projectId;
}
