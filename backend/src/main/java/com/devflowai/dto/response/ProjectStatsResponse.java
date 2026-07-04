package com.devflowai.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ProjectStatsResponse {
    private long totalProjects;
    private long completedProjects;
    private long inProgressProjects;
    private long pendingProjects;
}
