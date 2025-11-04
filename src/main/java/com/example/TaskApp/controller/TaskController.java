package com.example.TaskApp.controller;

import com.example.TaskApp.dto.Response;
import com.example.TaskApp.dto.TaskRequest;
import com.example.TaskApp.dto.TaskSummary;
import com.example.TaskApp.entity.Task;
import com.example.TaskApp.entity.User;
import com.example.TaskApp.exceptions.NotFoundException;
import com.example.TaskApp.repo.TaskRepository;
import com.example.TaskApp.security.AuthUser;
import com.example.TaskApp.service.EmailService;
import com.example.TaskApp.service.TaskService;
import com.example.TaskApp.service.UserService;
import com.example.TaskApp.service.EmailScheduler.TaskReminderStatus;
import com.example.TaskApp.service.EmailScheduler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(
        origins = {"https://taskapp-frontend-8x0n.onrender.com"} ,
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS},
        allowedHeaders = "*",
        allowCredentials = "true"
)
@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Slf4j
public class TaskController {

    private final TaskService taskService;
    private final UserService userService;
    private final EmailScheduler emailScheduler;
    private final EmailService emailService;

    @Autowired
    private TaskRepository taskRepository;

    @PostMapping
    public ResponseEntity<Response<Task>> createTask(@Valid @RequestBody TaskRequest taskRequest,
                                                     @AuthenticationPrincipal AuthUser authUser) {
        User user = authUser.getUser();
        return ResponseEntity.ok(taskService.createTask(taskRequest, user));
    }

    @PostMapping("/trigger-reminder")
    public ResponseEntity<String> triggerReminderManually() {
        emailScheduler.sendDueTaskReminders();
        return ResponseEntity.ok("Reminder job triggered manually");
    }

    @PutMapping("/{id}")
    public ResponseEntity<Response<Task>> updateTask(@PathVariable Long id,
                                                     @RequestBody TaskRequest taskRequest,
                                                     @AuthenticationPrincipal AuthUser authUser) {
        User user = authUser.getUser();
        return ResponseEntity.ok(taskService.updateTask(id, taskRequest, user));
    }

    @GetMapping("/all")
    public ResponseEntity<Response<List<Task>>> getAllMyTasks(@AuthenticationPrincipal AuthUser authUser) {
        log.info("🔍 Authenticated principal: {}", authUser.getUsername());

        if (authUser == null) {
            log.warn("❌ AuthUser is null — rejecting request");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Response.<List<Task>>builder()
                            .statusCode(401)
                            .message("Unauthorized: No authenticated user")
                            .data(null)
                            .build());
        }

        User user = authUser.getUser();
        log.info("🧠 Authenticated user email: {}", user.getEmail());

        if (user == null) {
            log.warn("❌ AuthUser.getUser() returned null — rejecting request");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Response.<List<Task>>builder()
                            .statusCode(401)
                            .message("Unauthorized: No user bound to AuthUser")
                            .data(null)
                            .build());
        }

        return ResponseEntity.ok(taskService.getAllMyTasks(user));
    }

    @GetMapping("/task/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id, @AuthenticationPrincipal AuthUser authUser) {
        User user = authUser.getUser();
        Task task = taskRepository.findById(id)
                .filter(t -> t.getUser().equals(user))
                .orElseThrow(() -> new NotFoundException("Task not found or unauthorized"));
        return ResponseEntity.ok(task);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<Task>> getTaskByIdSimple(@PathVariable Long id, @AuthenticationPrincipal AuthUser authUser) {
        User user = authUser.getUser();
        Task task = taskRepository.findById(id)
                .filter(t -> t.getUser().equals(user))
                .orElseThrow(() -> new NotFoundException("Task not found or unauthorized"));

        return ResponseEntity.ok(Response.<Task>builder()
                .statusCode(200)
                .message("Task retrieved successfully")
                .data(task)
                .build());
    }

    @DeleteMapping("/task/{id}")
    public ResponseEntity<Response<Void>> deleteTask(@PathVariable Long id,
                                                     @AuthenticationPrincipal AuthUser authUser) {
        User user = authUser.getUser();
        return ResponseEntity.ok(taskService.deleteTask(id, user));
    }

    @GetMapping("/status")
    public ResponseEntity<Response<List<Task>>> getMyTasksByCompletionStatus(@RequestParam Boolean completed,
                                                                             @AuthenticationPrincipal AuthUser authUser) {
        User user = authUser.getUser();
        return ResponseEntity.ok(taskService.getMyTasksByCompletionStatus(completed, user));
    }

    @GetMapping("/priority")
    public ResponseEntity<Response<List<Task>>> getMyTasksByPriority(@RequestParam String priority,
                                                                     @AuthenticationPrincipal AuthUser authUser) {
        User user = authUser.getUser();
        return ResponseEntity.ok(taskService.getMyTasksByPriority(priority, user));
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<Task>> getTasksDueTodayAndOverdue(@AuthenticationPrincipal AuthUser authUser) {
        User user = authUser.getUser();
        List<Task> overdueTasks = taskService.getTasksDueTodayAndOverdue(user);
        return ResponseEntity.ok(overdueTasks);
    }

    @GetMapping("/search")
    public ResponseEntity<Response<List<Task>>> findByTitle(@RequestParam String title,
                                                            @AuthenticationPrincipal AuthUser authUser) {
        User user = authUser.getUser();
        List<Task> tasks = taskService.findByTitleContainingAndUser(title, user);

        return ResponseEntity.ok(Response.<List<Task>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Search results retrieved successfully")
                .data(tasks)
                .build());
    }

    @GetMapping("/summary")
    public ResponseEntity<Response<TaskSummary>> getTaskSummary(@AuthenticationPrincipal AuthUser authUser) {
        User user = authUser.getUser();
        TaskSummary summary = taskService.getTaskSummary(user);
        return ResponseEntity.ok(Response.<TaskSummary>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Task summary retrieved successfully")
                .data(summary)
                .build());
    }

    @GetMapping("/reminder-status")
    public ResponseEntity<Response<List<TaskReminderStatus>>> getReminderStatus() {
        List<TaskReminderStatus> statusList = emailScheduler.getLastRunStatus();

        return ResponseEntity.ok(Response.<List<TaskReminderStatus>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Reminder status retrieved successfully")
                .data(statusList)
                .build());
    }

    @GetMapping("/test-email")
    public ResponseEntity<String> testEmail() {
        log.info("📨 /test-email endpoint triggered");
        emailService.sendReminderEmail(
                "sarikagsarika202@gmail.com",
                "Task Reminder",
                "Hi Sarika,\n\nJust a quick reminder that your scheduled task is due today.\n\nStay focused and keep crushing it!\n\n– TaskApp"
        );
        return ResponseEntity.ok("Test email triggered");
    }
}