package com.example.TaskApp.service.impl;

import com.example.TaskApp.dto.Response;
import com.example.TaskApp.dto.TaskRequest;
import com.example.TaskApp.dto.TaskSummary;
import com.example.TaskApp.entity.Task;
import com.example.TaskApp.entity.User;
import com.example.TaskApp.enums.Priority;
import com.example.TaskApp.enums.TaskStatus;
import com.example.TaskApp.exceptions.NotFoundException;
import com.example.TaskApp.repo.TaskRepository;
import com.example.TaskApp.service.TaskService;
import com.example.TaskApp.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TasksServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final EmailService emailService;

    @Override
    @Transactional
    public Response<Task> createTask(TaskRequest taskRequest, User user) {
        log.info("INSIDE createTask() for user: {}", user.getEmail());

        Task taskToSave = Task.builder()
                .title(taskRequest.getTitle())
                .description(taskRequest.getDescription())
                .completed(taskRequest.getCompleted() != null ? taskRequest.getCompleted() : false)
                .priority(taskRequest.getPriority())
                .status(TaskStatus.TO_DO)
                .dueDate(taskRequest.getDueDate())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .reminderSent(false)
                .user(user)
                .build();

        Task savedTask = taskRepository.save(taskToSave);

        return Response.<Task>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Task Created Successfully")
                .data(savedTask)
                .build();
    }

    @Override
    public Response<List<Task>> getAllMyTasks(User user) {
        log.info("🔍 getAllMyTasks called for user: {}", user != null ? user.getEmail() : "null");

        List<Task> tasks = taskRepository.findByUserOrderByDueDateAsc(user);

        return Response.<List<Task>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Tasks retrieved successfully")
                .data(tasks)
                .build();
    }

    @Override
    public Response<Task> getTaskById(Long id, User user) {
        log.info("inside getTaskById() for user: {}", user.getEmail());

        Task task = taskRepository.findById(id)
                .filter(t -> t.getUser().equals(user))
                .orElseThrow(() -> new NotFoundException("Task not found"));

        return Response.<Task>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Task retrieved successfully")
                .data(task)
                .build();
    }

    @Override
    public Response<Task> updateTask(Long id, TaskRequest taskRequest, User user) {
        Task task = taskRepository.findById(id)
                .filter(t -> t.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new NotFoundException("Task not found"));

        if (taskRequest.getTitle() != null) task.setTitle(taskRequest.getTitle());
        if (taskRequest.getDescription() != null) task.setDescription(taskRequest.getDescription());
        if (taskRequest.getPriority() != null) task.setPriority(taskRequest.getPriority());
        if (taskRequest.getDueDate() != null) task.setDueDate(taskRequest.getDueDate());
        if (taskRequest.getCompleted() != null) task.setCompleted(taskRequest.getCompleted());

        task.setUpdatedAt(LocalDateTime.now());

        Task updatedTask = taskRepository.save(task);

        return Response.<Task>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Task updated successfully")
                .data(updatedTask)
                .build();
    }

    @Override
    public Response<Void> deleteTask(Long id, User user) {
        log.info("inside deleteTask() for user: {}", user.getEmail());

        Task task = taskRepository.findById(id)
                .filter(t -> t.getUser().equals(user))
                .orElseThrow(() -> new NotFoundException("Task does not exist"));

        taskRepository.delete(task);

        return Response.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Task deleted successfully")
                .build();
    }

    @Override
    public Response<List<Task>> getMyTasksByCompletionStatus(Boolean completed, User user) {
        log.info("inside getMyTasksByCompletionStatus() for user: {}", user.getEmail());

        List<Task> tasks = taskRepository.findByCompletedAndUser(completed, user);

        return Response.<List<Task>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Tasks filtered by completion status")
                .data(tasks)
                .build();
    }

    @Override
    public Response<List<Task>> getMyTasksByPriority(String priority, User user) {
        log.info("inside getMyTasksByPriority() for user: {}", user.getEmail());

        Priority priorityEnum = Priority.valueOf(priority.toUpperCase());

        List<Task> tasks = taskRepository.findByPriorityAndUser(priorityEnum, user,
                org.springframework.data.domain.Sort.by("dueDate").ascending());

        return Response.<List<Task>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Tasks filtered by priority")
                .data(tasks)
                .build();
    }

    @Override
    public List<Task> findByTitleContainingAndUser(String title, User user) {
        return taskRepository.findByTitleContainingAndUser(title, user);
    }

    @Override
    public TaskSummary getTaskSummary(User user) {
        long totalTasks = taskRepository.countByUser(user);
        long completedTasks = taskRepository.countByCompletedAndUser(true, user);
        long highPriorityTasks = taskRepository.countByPriorityAndUser(Priority.HIGH, user);

        double completionPercentage = (totalTasks > 0)
                ? ((double) completedTasks / totalTasks) * 100
                : 0.0;

        return TaskSummary.builder()
                .totalTasks(totalTasks)
                .completedTasks(completedTasks)
                .pendingTasks(totalTasks - completedTasks)
                .highPriorityTasks(highPriorityTasks)
                .completionPercentage(completionPercentage)
                .build();
    }

    @Override
    public List<Task> getTasksDueTodayAndOverdue(User user) {
        log.info("Inside getTasksDueTodayAndOverdue() for user: {}", user.getEmail());
        LocalDate today = LocalDate.now();

        return taskRepository.findByUserAndCompletedFalseAndDueDateLessThanEqual(user, today);
    }
}