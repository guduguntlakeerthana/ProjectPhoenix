package com.devflowai.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AdminStatsResponse {
    private long totalUsers;
    private long totalProjects;
    private long totalTasks;
    private long totalAttachments;
}
