package com.devflowai.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AiChatResponse {
    private String response;
    private boolean simulated;
}
