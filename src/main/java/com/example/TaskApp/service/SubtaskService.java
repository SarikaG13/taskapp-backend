package com.example.TaskApp.service;

import com.example.TaskApp.dto.SubtaskRequest;
import com.example.TaskApp.entity.Subtask;
import com.example.TaskApp.entity.Task;
import com.example.TaskApp.entity.User;

import java.util.List;

public interface SubtaskService {

    Subtask createSubtask(SubtaskRequest subtaskRequest, Task task);

    List<Subtask> getSubtasksByTask(Task task);

    void deleteSubtask(Long subtaskId, User user);

    Subtask updateSubtask(Long subtaskId, SubtaskRequest request, User user);

    Subtask toggleSubtaskCompletion(Long subtaskId, User user);
}