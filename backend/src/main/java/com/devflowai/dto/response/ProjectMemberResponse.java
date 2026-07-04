package com.devflowai.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ProjectMemberResponse {
    private Long id;
    private String email;
    private String role;
    private Long projectId;
    private String projectTitle;
    private LocalDateTime invitedAt;
}
