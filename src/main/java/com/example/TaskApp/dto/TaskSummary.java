package com.example.TaskApp.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TaskSummary {
    private long totalTasks;
    private long completedTasks;
    private long pendingTasks;
    private long highPriorityTasks;
    private double completionPercentage;
}