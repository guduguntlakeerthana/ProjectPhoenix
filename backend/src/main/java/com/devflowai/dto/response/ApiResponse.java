package com.devflowai.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ApiResponse {

    private boolean success;
    private String message;
    private LocalDateTime timestamp;
}