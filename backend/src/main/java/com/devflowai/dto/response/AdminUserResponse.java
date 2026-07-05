package com.devflowai.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class AdminUserResponse {
    private Long id;
    private String fullName;
    private String email;
    private String role;
    private LocalDateTime createdAt;
}
