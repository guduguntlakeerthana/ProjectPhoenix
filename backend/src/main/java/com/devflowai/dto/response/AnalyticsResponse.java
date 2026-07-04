package com.devflowai.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class AnalyticsResponse {
    private long totalProjects;
    private long completedProjects;
    private long inProgressProjects;
    private long pendingProjects;

    private long totalTasks;
    private long todoTasks;
    private long inProgressTasks;
    private long completedTasks;

    private long lowPriorityTasks;
    private long mediumPriorityTasks;
    private long highPriorityTasks;

    private List<ProjectResponse> recentProjects;
    private List<TaskResponse> recentTasks;
}
