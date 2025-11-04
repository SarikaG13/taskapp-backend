package com.example.TaskApp.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class SubtaskRequest {

    @NotNull
    private Long taskId;

    @NotBlank(message = "Subtask title is required")
    private String title;

    private Boolean completed;
}