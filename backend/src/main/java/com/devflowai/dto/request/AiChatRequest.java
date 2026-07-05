package com.devflowai.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AiChatRequest {

    @NotBlank(message = "Prompt cannot be blank")
    private String prompt;
}
