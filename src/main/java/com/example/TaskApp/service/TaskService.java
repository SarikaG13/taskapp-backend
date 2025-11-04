package com.example.TaskApp.service;

import com.example.TaskApp.dto.Response;
import com.example.TaskApp.dto.TaskRequest;
import com.example.TaskApp.dto.TaskSummary;
import com.example.TaskApp.entity.Task;
import com.example.TaskApp.entity.User;

import java.util.List;

public interface TaskService {

    Response<Task> createTask(TaskRequest request, User user);

    Response<List<Task>> getAllMyTasks(User user);

    Response<Task> getTaskById(Long id, User user);

    Response<Task> updateTask(Long id, TaskRequest request, User user);

    Response<Void> deleteTask(Long id, User user);

    Response<List<Task>> getMyTasksByCompletionStatus(Boolean completed, User user);

    Response<List<Task>> getMyTasksByPriority(String priority, User user);

    List<Task> findByTitleContainingAndUser(String title, User user);

    TaskSummary getTaskSummary(User user);

    List<Task> getTasksDueTodayAndOverdue(User user);
}