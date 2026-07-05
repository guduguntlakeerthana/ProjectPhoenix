package com.devflowai.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ReportResponse {
    private long totalProjects;
    private long totalTasks;
    private long completedTasks;
    private long inProgressTasks;
    private long reviewTasks;
    private long todoTasks;
    private double averageTaskProgress;
    private long highPriorityTasks;
    private long mediumPriorityTasks;
    private long lowPriorityTasks;
}
